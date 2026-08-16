/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.provider.smb.client

import com.hierynomus.mserref.NtStatus
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.common.SMBRuntimeException
import com.hierynomus.smbj.share.File
import com.hierynomus.smbj.share.FileAccessor
import me.zhanghai.android.files.provider.common.AbstractFileByteChannel
import me.zhanghai.android.files.provider.common.EMPTY
import me.zhanghai.android.files.provider.common.map
import me.zhanghai.android.files.util.closeSafe
import me.zhanghai.android.files.util.findCauseByClass
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.ClosedByInterruptException
import java.util.ArrayDeque
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

class FileByteChannel(
    private val file: File,
    isAppend: Boolean
// Cancelling reads leads to TransportException: Received response with unknown sequence number
) : AbstractFileByteChannel(isAppend, shouldCancelRead = false) {
    private val pendingWrites = ArrayDeque<PendingWrite>()

    @Throws(IOException::class)
    override fun onReadAsync(position: Long, size: Int, timeoutMillis: Long): Future<ByteBuffer> =
        try {
            FileAccessor.readAsync(file, position, size)
        } catch (e: SMBRuntimeException) {
            throw e.toIOException()
        }
            .map(
                { response ->
                    when (response.header.statusCode) {
                        NtStatus.STATUS_END_OF_FILE.value -> {
                            return@map ByteBuffer::class.EMPTY
                        }
                        NtStatus.STATUS_SUCCESS.value -> {}
                        else -> throw SMBApiException(response.header, "Read failed for $this")
                            .toIOException()
                    }
                    val data = response.data
                    if (data.isEmpty()) {
                        return@map ByteBuffer::class.EMPTY
                    }
                    val length = data.size.coerceAtMost(size)
                    ByteBuffer.wrap(data, 0, length)
                }, { e ->
                    ExecutionException(SMBRuntimeException(e).toIOException())
                }
            )

    @Throws(IOException::class)
    override fun onWrite(position: Long, source: ByteBuffer) {
        if (pendingWrites.size >= WRITE_PIPELINE_DEPTH) {
            awaitFirstPendingWrite()
        }

        // The caller reuses its transfer buffer as soon as this method returns, so retain an
        // immutable copy until SMBJ has completed the asynchronous request.
        val size = source.remaining()
        val bytes = ByteArray(size)
        source.duplicate().get(bytes)
        val future = try {
            file.writeAsync(bytes, position, 0, size)
        } catch (e: SMBRuntimeException) {
            throw e.toIOException()
        }
        pendingWrites += PendingWrite(size, future)
        source.position(source.position() + size)
    }

    @Throws(IOException::class)
    override fun onTruncate(size: Long) {
        awaitPendingWrites()
        try {
            file.setLength(size)
        } catch (e: SMBRuntimeException) {
            throw e.toIOException()
        }
    }

    @Throws(IOException::class)
    override fun onSize(): Long =
        try {
            awaitPendingWrites()
            file.getFileInformation(FileStandardInformation::class.java).endOfFile
        } catch (e: SMBRuntimeException) {
            throw e.toIOException()
        }

    @Throws(IOException::class)
    override fun onForce(metaData: Boolean) {
        awaitPendingWrites()
        try {
            file.flush()
        } catch (e: SMBRuntimeException) {
            throw e.toIOException()
        }
    }

    private fun SMBRuntimeException.toIOException(): IOException =
        when {
            findCauseByClass<SMBApiException>()
                .let { it != null && it.status == NtStatus.STATUS_FILE_CLOSED } -> {
                setClosed()
                AsynchronousCloseException().apply { initCause(this@toIOException) }
            }
            findCauseByClass<InterruptedException>() != null -> {
                closeSafe()
                ClosedByInterruptException().apply { initCause(this@toIOException) }
            }
            else -> IOException(this)
        }

    @Throws(IOException::class)
    override fun onClose() {
        var exception: IOException? = null
        try {
            awaitPendingWrites()
        } catch (e: IOException) {
            exception = e
        }
        try {
            file.close()
        } catch (e: SMBRuntimeException) {
            val closeException = when {
                e.findCauseByClass<InterruptedException>() != null ->
                    InterruptedIOException().apply { initCause(e) }
                else -> IOException(e)
            }
            if (exception == null) {
                exception = closeException
            } else {
                exception.addSuppressed(closeException)
            }
        }
        exception?.let { throw it }
    }

    @Throws(IOException::class)
    private fun awaitPendingWrites() {
        var exception: IOException? = null
        while (pendingWrites.isNotEmpty()) {
            try {
                awaitFirstPendingWrite()
            } catch (e: IOException) {
                if (exception == null) {
                    exception = e
                } else {
                    exception.addSuppressed(e)
                }
            }
        }
        exception?.let { throw it }
    }

    @Throws(IOException::class)
    private fun awaitFirstPendingWrite() {
        val pendingWrite = pendingWrites.removeFirst()
        val bytesWritten = try {
            pendingWrite.future.get()
        } catch (e: CancellationException) {
            throw InterruptedIOException().apply { initCause(e) }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedIOException().apply { initCause(e) }
        } catch (e: ExecutionException) {
            val cause = e.cause ?: e
            throw if (cause is SMBRuntimeException) cause.toIOException() else IOException(cause)
        }
        if (bytesWritten != pendingWrite.size) {
            throw IOException("Incomplete SMB write: $bytesWritten of ${pendingWrite.size} bytes")
        }
    }

    private data class PendingWrite(val size: Int, val future: Future<Int>)

    companion object {
        // Bound memory use while keeping enough SMB requests in flight to hide LAN latency.
        private const val WRITE_PIPELINE_DEPTH = 4
    }
}
