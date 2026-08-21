/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 * Modified 2026-08-20 for FM Plus Ultra.
 */

package me.zhanghai.android.files.provider.smb.client

import android.os.SystemClock
import android.util.Log
import com.hierynomus.mserref.NtStatus
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.common.SMBRuntimeException
import com.hierynomus.smbj.share.File
import com.hierynomus.smbj.share.FileAccessor
import me.zhanghai.android.files.BuildConfig
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
    isAppend: Boolean,
    private val initialReadSizeHint: Long? = null
// Cancelling reads leads to TransportException: Received response with unknown sequence number.
// Keep multiple ordered reads in flight so receive latency does not stall each block.
) : AbstractFileByteChannel(
    isAppend,
    shouldCancelRead = false,
    readPipelineDepth = READ_PIPELINE_DEPTH,
    readBufferSize = READ_BUFFER_SIZE,
    initialReadSizeHint = initialReadSizeHint
) {
    private val pendingWrites = ArrayDeque<PendingWrite>()

    @Throws(IOException::class)
    override fun onReadAsync(position: Long, size: Int, timeoutMillis: Long): Future<ByteBuffer> {
        val submittedElapsedMillis = SystemClock.elapsedRealtime()
        return try {
            logRequestState("READ_SUBMIT", position, size, READ_PIPELINE_DEPTH)
            FileAccessor.readAsync(file, position, size)
        } catch (e: SMBRuntimeException) {
            throw e.toIOException()
        }
            .map(
                { response ->
                    when (response.header.statusCode) {
                        NtStatus.STATUS_END_OF_FILE.value -> {
                            logRequestState(
                                "READ_COMPLETE", position, 0, READ_PIPELINE_DEPTH,
                                SystemClock.elapsedRealtime() - submittedElapsedMillis
                            )
                            return@map ByteBuffer::class.EMPTY
                        }
                        NtStatus.STATUS_SUCCESS.value -> {}
                        else -> throw SMBApiException(response.header, "Read failed for $this")
                            .toIOException()
                    }
                    val data = response.data
                    logRequestState(
                        "READ_COMPLETE", position, data.size, READ_PIPELINE_DEPTH,
                        SystemClock.elapsedRealtime() - submittedElapsedMillis
                    )
                    if (data.isEmpty()) {
                        return@map ByteBuffer::class.EMPTY
                    }
                    val length = data.size.coerceAtMost(size)
                    ByteBuffer.wrap(data, 0, length)
                }, { e ->
                    ExecutionException(SMBRuntimeException(e).toIOException())
                }
            )
    }

    override fun onReadTimedOut(position: Long, timeoutMillis: Long) {
        Log.w(
            SMB_IO_TAG,
            "READ_TIMEOUT offset=$position timeoutMs=$timeoutMillis; closing stalled connection"
        )
        try {
            file.diskShare.treeConnect.session.connection.close(true)
        } catch (_: IOException) {
        }
    }

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
        pendingWrites += PendingWrite(
            position, size.toLong(), SystemClock.elapsedRealtime(), future
        )
        logRequestState("WRITE_SUBMIT", position, size, pendingWrites.size)
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
        } catch (e: IllegalStateException) {
            if (e.message != TRANSPORT_NOT_CONNECTED_MESSAGE) {
                throw e
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
        logRequestState(
            "WRITE_COMPLETE",
            pendingWrite.position,
            pendingWrite.size.toInt(),
            pendingWrites.size,
            SystemClock.elapsedRealtime() - pendingWrite.submittedElapsedMillis
        )
    }

    private fun logRequestState(
        action: String,
        position: Long,
        size: Int,
        inFlight: Int,
        requestAgeMillis: Long? = null
    ) {
        if (!BuildConfig.DEBUG || position % REQUEST_LOG_INTERVAL != 0L &&
            (requestAgeMillis == null || requestAgeMillis < SLOW_REQUEST_LOG_MILLIS)) {
            return
        }
        Log.i(
            SMB_IO_TAG,
            "$action configuration=$IO_CONFIGURATION offset=$position size=$size " +
                "inFlight=$inFlight " +
                "sizeHint=${initialReadSizeHint ?: "none"} " +
                "availableCredits=${FileAccessor.getAvailableCredits(file)}" +
                if (requestAgeMillis != null) " requestAgeMs=$requestAgeMillis" else ""
        )
    }

    private data class PendingWrite(
        val position: Long,
        val size: Long,
        val submittedElapsedMillis: Long,
        val future: Future<Long>
    )

    companion object {
        // Native AES-CMAC removed the prior per-byte CPU ceiling. Eight 256 KiB requests expose up
        // to 2 MiB of useful receive window while keeping individual responses small and bounded.
        private const val READ_BUFFER_SIZE = 256 * 1024
        private const val READ_PIPELINE_DEPTH = 8
        // Four 256 KiB writes allow socket output and response handling to overlap without the old
        // 6 MiB backlog; the maximum retained write data is only 1 MiB.
        private const val WRITE_PIPELINE_DEPTH = 4
        private const val IO_CONFIGURATION =
            "async/native/read:256k-x8-size-aware/write:256k-x4"
        private const val REQUEST_LOG_INTERVAL = 64L * 1024 * 1024
        private const val SLOW_REQUEST_LOG_MILLIS = 1_000L
        private const val TRANSPORT_NOT_CONNECTED_MESSAGE = "Transport is not connected"
        private const val SMB_IO_TAG = "FMPU.SmbIo"
    }
}
