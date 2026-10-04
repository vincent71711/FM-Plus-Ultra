/*
 * Copyright (c) 2026 FM Plus Ultra contributors
 * SPDX-License-Identifier: GPL-3.0-only
 * Added 2026-10-04 for FM Plus Ultra.
 */
package me.zhanghai.android.files.provider.common

import com.hierynomus.smbj.common.SMBRuntimeException
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class StreamingRecoveryTest {
    @Test
    fun smbMappedTimeoutReachesRecoveryHook() {
        val channel = TestChannel()
        val raw = object : CompletableFuture<ByteBuffer>() {
            override fun get(timeout: Long, unit: TimeUnit): ByteBuffer = throw TimeoutException()
        }
        // Use SMB's actual exception wrapping shape; waiting control signals must bypass it.
        channel.next = raw.map({ it }, { ExecutionException(IOException(SMBRuntimeException(it))) })
        assertThrows(FileReadTimeoutException::class.java) { channel.read(ByteBuffer.allocate(4)) }
        assertEquals(1, channel.timeouts)
        assertEquals(0L, channel.position())
        assertArrayEquals(byteArrayOf(0, 1, 2, 3), channel.readBlock())
        channel.close()
    }

    @Test
    fun failedReadDoesNotSkipDataAndSeekingStillWorks() {
        val channel = TestChannel()
        channel.next = CompletableFuture<ByteBuffer>().apply {
            completeExceptionally(IOException("synthetic network failure"))
        }
        assertThrows(IOException::class.java) { channel.read(ByteBuffer.allocate(4)) }
        assertEquals(0L, channel.position())
        assertArrayEquals(byteArrayOf(0, 1, 2, 3), channel.readBlock())
        assertArrayEquals(byteArrayOf(4, 5, 6, 7), channel.readBlock())
        channel.position(2)
        assertArrayEquals(byteArrayOf(2, 3, 4, 5), channel.readBlock())
        channel.close()
    }

    @Test
    fun failedSpeculativeSubmissionDoesNotSkipData() {
        val channel = TestChannel()
        channel.failSubmissionAt = 8
        assertThrows(IOException::class.java) { channel.read(ByteBuffer.allocate(4)) }
        assertEquals(0L, channel.position())
        assertArrayEquals(byteArrayOf(0, 1, 2, 3), channel.readBlock())
        channel.close()
    }

    @Test(timeout = 5000)
    fun concurrentCloseDoesNotDeadlockReadError() {
        val entered = CountDownLatch(1)
        val failRead = CountDownLatch(1)
        val failure = AtomicReference<Throwable>()
        val channel = object : TestChannel() {
            override fun onReadAsync(position: Long, size: Int, timeoutMillis: Long): Future<ByteBuffer> {
                entered.countDown()
                check(failRead.await(2, TimeUnit.SECONDS))
                setClosed() // Same lock acquisition as SMB STATUS_FILE_CLOSED conversion.
                throw IOException("synthetic closed handle")
            }
        }
        val reader = thread(isDaemon = true) {
            try {
                channel.readBlock()
                failure.set(AssertionError("Expected read error"))
            } catch (_: IOException) {
            } catch (t: Throwable) {
                failure.set(t)
            }
        }
        assertTrue(entered.await(2, TimeUnit.SECONDS))
        val closer = thread(isDaemon = true) { channel.close() }
        try {
            // Wait until close is blocked on the read's I/O monitor, without taking closeLock.
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2)
            while (closer.state != Thread.State.BLOCKED && System.nanoTime() < deadline) {
                Thread.yield()
            }
            assertEquals(Thread.State.BLOCKED, closer.state)
        } finally {
            failRead.countDown()
        }
        reader.join(1000)
        closer.join(1000)
        assertFalse("read deadlocked", reader.isAlive)
        assertFalse("close deadlocked", closer.isAlive)
        assertNull(failure.get())
        assertEquals(1, channel.closes)
    }

    private open class TestChannel : AbstractFileByteChannel(
        isAppend = false, shouldCancelRead = false, readPipelineDepth = 2,
        readBufferSize = 4, initialReadSizeHint = 16
    ) {
        var next: Future<ByteBuffer>? = null
        var failSubmissionAt: Long? = null
        var timeouts = 0
        var closes = 0

        override fun onReadAsync(position: Long, size: Int, timeoutMillis: Long): Future<ByteBuffer> {
            if (position == failSubmissionAt) {
                failSubmissionAt = null
                throw IOException("synthetic submission failure")
            }
            next?.let { next = null; return it }
            return CompletableFuture.completedFuture(
                ByteBuffer.wrap(ByteArray(size.coerceAtMost((16 - position).coerceAtLeast(0).toInt())) {
                    (position + it).toByte()
                })
            )
        }
        override fun onReadTimedOut(position: Long, timeoutMillis: Long) { ++timeouts }
        override fun onSize(): Long = 16
        override fun onWrite(position: Long, source: ByteBuffer) { error("read-only fixture") }
        override fun onTruncate(size: Long) { error("read-only fixture") }
        override fun onClose() { ++closes }
        fun readBlock(): ByteArray = ByteBuffer.allocate(4).also { assertEquals(4, read(it)) }.array()
    }
}
