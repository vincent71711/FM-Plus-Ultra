/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filejob

import android.os.SystemClock
import android.util.Log
import me.zhanghai.android.files.BuildConfig
import me.zhanghai.android.files.util.showToast
import java.io.IOException
import java.io.InterruptedIOException
import java.util.Random

abstract class FileJob {
    val id = Random().nextInt()

    internal lateinit var service: FileJobService
        private set

    fun runOn(service: FileJobService) {
        this.service = service
        val startElapsedRealtime = SystemClock.elapsedRealtime()
        if (BuildConfig.DEBUG) {
            Log.i(TRANSFER_TIMING_TAG, "START job=${javaClass.simpleName} id=$id")
        }
        try {
            run()
            // TODO: Toast
        } catch (e: InterruptedIOException) {
            // TODO
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
            service.showToast(e.toString())
        } finally {
            if (BuildConfig.DEBUG) {
                val elapsedMillis = SystemClock.elapsedRealtime() - startElapsedRealtime
                Log.i(
                    TRANSFER_TIMING_TAG,
                    "FINISH job=${javaClass.simpleName} id=$id elapsedMs=$elapsedMillis"
                )
            }
            service.notificationManager.cancel(id)
        }
    }

    @Throws(IOException::class)
    protected abstract fun run()

    private companion object {
        const val TRANSFER_TIMING_TAG = "FMPU.TransferTiming"
    }
}
