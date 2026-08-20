/*
 * Copyright (c) 2024 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 * Modified 2026-08-20 for FM Plus Ultra.
 */

package me.zhanghai.android.files.provider.common

import java8.nio.channels.SeekableByteChannel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withTimeout
import me.zhanghai.android.files.util.closeSafe
import java.io.Closeable
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.NonReadableChannelException
import java.util.ArrayDeque
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FileReadTimeoutException(
    val filePosition: Long,
    val timeoutMillis: Long
) : IOException("File read at offset $filePosition timed out after $timeoutMillis ms")

abstract class AbstractFileByteChannel(
    private val isAppend: Boolean,
    private val shouldCancelRead: Boolean = true,
    private val joinCancelledRead: Boolean = false,
    private val readPipelineDepth: Int = 1,
    private val readBufferSize: Int = DEFAULT_READ_BUFFER_SIZE,
    private val initialReadSizeHint: Long? = null
) : ForceableChannel, SeekableByteChannel {
    private var position = 0L
    private val readBuffer = ReadBuffer()
    private val ioLock = Any()

    private var isOpen = true
    private val closeLock = Any()

    init {
        require(readPipelineDepth >= 1)
        require(readBufferSize >= 1)
        require(initialReadSizeHint == null || initialReadSizeHint >= 0)
    }

    @Throws(IOException::class)
    final override fun read(destination: ByteBuffer): Int {
        ensureOpen()
        if (isAppend) {
            throw NonReadableChannelException()
        }
        val remaining = destination.remaining()
        if (remaining == 0) {
            return 0
        }
        return synchronized(ioLock) {
            readBuffer.read(destination).also {
                if (it != -1) {
                    position += it
                }
            }
        }
    }

    protected open fun onReadAsync(
        position: Long,
        size: Int,
        timeoutMillis: Long
    ): Future<ByteBuffer> =
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.async(Dispatchers.IO) {
            withTimeout(timeoutMillis) {
                runInterruptible {
                    onRead(position, size)
                }
            }
        }
            .asFuture()

    @Throws(IOException::class)
    protected open fun onRead(position: Long, size: Int): ByteBuffer {
        throw NotImplementedError()
    }

    protected open fun onReadTimedOut(position: Long, timeoutMillis: Long) {}

    @Throws(IOException::class)
    final override fun write(source: ByteBuffer): Int {
        ensureOpen()
        val remaining = source.remaining()
        if (remaining == 0) {
            return 0
        }
        synchronized(ioLock) {
            if (isAppend) {
                onAppend(source)
                position = onSize()
            } else {
                onWrite(position, source)
                position += remaining - source.remaining()
            }
            return remaining
        }
    }

    @Throws(IOException::class)
    protected abstract fun onWrite(position: Long, source: ByteBuffer)

    @Throws(IOException::class)
    protected open fun onAppend(source: ByteBuffer) {
        val position = onSize()
        onWrite(position, source)
    }

    @Throws(IOException::class)
    final override fun position(): Long {
        ensureOpen()
        synchronized(ioLock) {
            if (isAppend) {
                position = onSize()
            }
            return position
        }
    }

    final override fun position(newPosition: Long): SeekableByteChannel {
        ensureOpen()
        if (isAppend) {
            // Ignored.
            return this
        }
        synchronized(ioLock) {
            readBuffer.reposition(position, newPosition)
            position = newPosition
        }
        return this
    }

    @Throws(IOException::class)
    final override fun size(): Long {
        ensureOpen()
        return onSize()
    }

    @Throws(IOException::class)
    final override fun truncate(size: Long): SeekableByteChannel {
        ensureOpen()
        require(size >= 0)
        synchronized(ioLock) {
            val currentSize = onSize()
            if (size >= currentSize) {
                return this
            }
            onTruncate(size)
            position = position.coerceAtMost(size)
        }
        return this
    }

    @Throws(IOException::class)
    protected abstract fun onTruncate(size: Long)

    @Throws(IOException::class)
    protected abstract fun onSize(): Long

    @Throws(IOException::class)
    final override fun force(metaData: Boolean) {
        ensureOpen()
        synchronized(ioLock) {
            onForce(metaData)
        }
    }

    @Throws(IOException::class)
    protected open fun onForce(metaData: Boolean) {}

    @Throws(ClosedChannelException::class)
    private fun ensureOpen() {
        synchronized(closeLock) {
            if (!isOpen) {
                throw ClosedChannelException()
            }
        }
    }

    final override fun isOpen(): Boolean = synchronized(closeLock) { isOpen }

    @Throws(IOException::class)
    final override fun close() {
        synchronized(closeLock) {
            if (!isOpen) {
                return
            }
            isOpen = false
            synchronized(ioLock) {
                readBuffer.closeSafe()
                onClose()
            }
        }
    }

    protected fun setClosed() {
        synchronized(closeLock) {
            isOpen = false
        }
    }

    @Throws(IOException::class)
    protected open fun onClose() {}

    private data class PendingRead(
        val position: Long,
        val future: Future<ByteBuffer>
    )

    private inner class ReadBuffer : Closeable {
        private val buffer = ByteBuffer.allocate(readBufferSize).apply { limit(0) }
        private var bufferedPosition = 0L
        private var nextReadPosition = 0L
        private var readSizeHint = initialReadSizeHint

        private val pendingReads = ArrayDeque<PendingRead>()

        @Throws(IOException::class)
        fun read(destination: ByteBuffer): Int {
            if (!buffer.hasRemaining()) {
                readIntoBuffer()
                if (!buffer.hasRemaining()) {
                    return -1
                }
            }
            val length = destination.remaining().coerceAtMost(buffer.remaining())
            val bufferLimit = buffer.limit()
            buffer.limit(buffer.position() + length)
            destination.put(buffer)
            buffer.limit(bufferLimit)
            return length
        }

        @Throws(IOException::class)
        private fun readIntoBuffer() {
            // When the size hint has been reached, allow one request to confirm EOF. This keeps
            // reads correct if another client grows the file after we opened it, without issuing
            // a full speculative pipeline beyond every normal file boundary.
            fillReadPipeline(allowSizeHintProbe = true)
            val pendingRead = pendingReads.removeFirst()
            val newBuffer = try {
                pendingRead.future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            } catch (e: CancellationException) {
                throw InterruptedIOException().apply { initCause(e) }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw InterruptedIOException().apply { initCause(e) }
            } catch (e: TimeoutException) {
                onReadTimedOut(pendingRead.position, TIMEOUT_MILLIS)
                throw FileReadTimeoutException(pendingRead.position, TIMEOUT_MILLIS).apply {
                    initCause(e)
                }
            } catch (e: ExecutionException) {
                val exception = e.cause ?: e
                if (exception is IOException) {
                    throw exception
                } else {
                    throw IOException(exception)
                }
            }
            buffer.clear()
            buffer.put(newBuffer)
            buffer.flip()
            if (!buffer.hasRemaining()) {
                cancelPendingReads()
                return
            }
            if (readSizeHint?.let { pendingRead.position >= it } == true) {
                // The EOF probe found data, so the original size is stale and must no longer bound
                // subsequent speculative reads.
                readSizeHint = null
            }
            bufferedPosition = pendingRead.position + buffer.remaining()
            if (buffer.remaining() < readBufferSize) {
                // A provider may legally return a short read. Discard speculative requests whose
                // positions assumed a full block, then continue from the actual end position.
                cancelPendingReads()
                nextReadPosition = bufferedPosition
            }
            fillReadPipeline(allowSizeHintProbe = false)
        }

        private fun fillReadPipeline(allowSizeHintProbe: Boolean) {
            while (pendingReads.size < readPipelineDepth) {
                val size = readSizeHint?.let { sizeHint ->
                    val remaining = sizeHint - nextReadPosition
                    when {
                        remaining > 0 -> remaining.coerceAtMost(readBufferSize.toLong()).toInt()
                        allowSizeHintProbe && pendingReads.isEmpty() -> readBufferSize
                        else -> 0
                    }
                } ?: readBufferSize
                if (size == 0) {
                    break
                }
                pendingReads += PendingRead(
                    nextReadPosition,
                    onReadAsync(nextReadPosition, size, TIMEOUT_MILLIS)
                )
                nextReadPosition += size
            }
        }

        fun reposition(oldPosition: Long, newPosition: Long) {
            if (newPosition == oldPosition) {
                return
            }
            val newBufferPosition = buffer.position() + (newPosition - oldPosition)
            if (newBufferPosition in 0..buffer.limit()) {
                buffer.position(newBufferPosition.toInt())
            } else {
                cancelPendingReads()
                buffer.limit(0)
                bufferedPosition = newPosition
                nextReadPosition = newPosition
            }
        }

        override fun close() {
            cancelPendingReads()
        }

        private fun cancelPendingReads() {
            while (pendingReads.isNotEmpty()) {
                pendingReads.removeFirst().future.let {
                    if (!shouldCancelRead) {
                        return@let
                    }
                    it.cancel(true)
                    if (joinCancelledRead) {
                        try {
                            it.get()
                        } catch (e: Exception) {
                            // Ignored
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_READ_BUFFER_SIZE = 1024 * 1024
        private const val TIMEOUT_MILLIS = 15_000L
    }
}
