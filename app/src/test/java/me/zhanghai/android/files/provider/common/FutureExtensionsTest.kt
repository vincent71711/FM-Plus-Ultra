/*
 * Copyright (c) 2026 FM Plus Ultra contributors
 * SPDX-License-Identifier: GPL-3.0-only
 * Added 2026-10-04 for FM Plus Ultra.
 */
package me.zhanghai.android.files.provider.common

import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FutureExtensionsTest {
    @Test
    fun timedWaitCanBeRetriedAfterCompletion() {
        val deferred = CompletableDeferred<Int>()
        val future = deferred.asFuture()
        assertThrows(TimeoutException::class.java) { future.get(1, TimeUnit.MILLISECONDS) }
        assertFalse(future.isDone)
        deferred.complete(42)
        assertEquals(42, future.get(1, TimeUnit.SECONDS))
    }

    @Test
    fun cancellationCancelsUnderlyingDeferred() {
        val deferred = CompletableDeferred<Int>()
        val future = deferred.asFuture()
        assertTrue(future.cancel(true))
        assertTrue(deferred.isCancelled)
        assertThrows(CancellationException::class.java) { future.get() }
    }

    @Test
    fun mappedWaitPreservesInterruptionAndCancellation() {
        val interrupted = object : CompletableFuture<Int>() {
            override fun get(): Int = throw InterruptedException("synthetic interruption")
        }.map({ it }, { ExecutionException(it) })
        assertThrows(InterruptedException::class.java) { interrupted.get() }
        val cancelled = CompletableFuture<Int>().apply { cancel(false) }
            .map({ it }, { ExecutionException(it) })
        assertThrows(CancellationException::class.java) { cancelled.get() }
    }
}
