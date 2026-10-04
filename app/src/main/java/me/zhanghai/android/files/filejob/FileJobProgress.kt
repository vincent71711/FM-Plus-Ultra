/*
 * Copyright (c) 2026 Vincent Frosceno
 *
 * This file is part of the FM Plus Ultra modifications.
 */

package me.zhanghai.android.files.filejob

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.zhanghai.android.files.BuildConfig

enum class FileJobProgressStatus {
    PREPARING,
    SCANNING,
    TRANSFERRING,
    CANCELLING,
    COMPLETED,
    CANCELLED,
    FAILED;

    val isFinished: Boolean
        get() = this == COMPLETED || this == CANCELLED || this == FAILED
}

data class FileJobProgress(
    val id: Int,
    val operation: String,
    val status: FileJobProgressStatus,
    val title: String = operation,
    val sourceLocation: String? = null,
    val targetLocation: String? = null,
    val currentItem: String? = null,
    val target: String? = null,
    val fileCount: Int = 0,
    val currentFileIndex: Int = 0,
    val totalBytes: Long = 0,
    val transferredBytes: Long = 0,
    val currentFileTotalBytes: Long = 0,
    val currentFileTransferredBytes: Long = 0,
    val bytesPerSecond: Long = 0,
    val remainingSeconds: Long? = null,
    val error: String? = null
) {
    val percent: Int?
        get() = if (totalBytes > 0) {
            ((transferredBytes.coerceIn(0, totalBytes) * 100) / totalBytes).toInt()
        } else {
            null
        }

    val currentFilePercent: Int?
        get() = if (currentFileTotalBytes > 0) {
            ((currentFileTransferredBytes.coerceIn(0, currentFileTotalBytes) * 100) /
                currentFileTotalBytes).toInt()
        } else {
            null
        }
}

object FileJobProgressStore {
    private data class Entry(
        var progress: FileJobProgress,
        val direction: String,
        val source: String,
        val target: String,
        var lastSampleBytes: Long = 0,
        var lastSampleElapsedMillis: Long = SystemClock.elapsedRealtime(),
        var transferStartedElapsedMillis: Long? = null,
        var lastDebugBytes: Long = 0,
        var lastDebugElapsedMillis: Long = 0
    )

    private val entries = linkedMapOf<Int, Entry>()
    private val mutableProgresses = MutableLiveData<List<FileJobProgress>>(emptyList())
    private val mutableVisibleDialogJobId = MutableLiveData<Int?>(null)
    private var visibleDialogJobId: Int? = null

    val progresses: LiveData<List<FileJobProgress>> = mutableProgresses
    val visibleDialog: LiveData<Int?> = mutableVisibleDialogJobId

    fun setDialogVisible(id: Int, visible: Boolean) {
        synchronized(entries) {
            if (visible) {
                visibleDialogJobId = id
            } else if (visibleDialogJobId == id) {
                visibleDialogJobId = null
            } else {
                return
            }
            mutableVisibleDialogJobId.postValue(visibleDialogJobId)
        }
    }

    fun start(
        id: Int,
        operation: String,
        direction: String,
        source: String,
        target: String
    ) {
        synchronized(entries) {
            entries[id] = Entry(
                FileJobProgress(
                    id, operation, FileJobProgressStatus.PREPARING,
                    sourceLocation = source, targetLocation = target
                ),
                direction,
                source,
                target
            )
            if (BuildConfig.DEBUG) {
                Log.i(
                    BENCHMARK_TAG,
                    "BEGIN id=$id operation=$operation direction=$direction source=$source " +
                        "target=$target"
                )
            }
            publishLocked()
        }
    }

    fun updateScanning(id: Int, title: String, fileCount: Int, totalBytes: Long) {
        synchronized(entries) {
            val entry = entries[id] ?: return
            if (entry.progress.status == FileJobProgressStatus.CANCELLING) {
                return
            }
            entry.progress = entry.progress.copy(
                status = FileJobProgressStatus.SCANNING,
                title = title,
                fileCount = fileCount,
                totalBytes = totalBytes
            )
            publishLocked()
        }
    }

    fun updateTransfer(
        id: Int,
        title: String,
        currentItem: String,
        target: String,
        fileCount: Int,
        currentFileIndex: Int,
        totalBytes: Long,
        transferredBytes: Long,
        currentFileTotalBytes: Long,
        currentFileTransferredBytes: Long
    ) {
        synchronized(entries) {
            val entry = entries[id] ?: return
            if (entry.progress.status == FileJobProgressStatus.CANCELLING) {
                return
            }
            val now = SystemClock.elapsedRealtime()
            val isFirstTransferUpdate = entry.transferStartedElapsedMillis == null
            if (isFirstTransferUpdate) {
                entry.transferStartedElapsedMillis = now
                entry.lastSampleBytes = transferredBytes
                entry.lastSampleElapsedMillis = now
                entry.lastDebugBytes = transferredBytes
                entry.lastDebugElapsedMillis = now
                if (BuildConfig.DEBUG) {
                    Log.i(
                        BENCHMARK_TAG,
                        "TRANSFER_START id=$id direction=${entry.direction} totalBytes=$totalBytes"
                    )
                }
            }
            val elapsedMillis = now - entry.lastSampleElapsedMillis
            var bytesPerSecond = if (isFirstTransferUpdate) 0 else entry.progress.bytesPerSecond
            if (!isFirstTransferUpdate && elapsedMillis >= MINIMUM_SAMPLE_MILLIS &&
                transferredBytes >= entry.lastSampleBytes) {
                val sampleBytesPerSecond =
                    (transferredBytes - entry.lastSampleBytes) * 1000 / elapsedMillis
                bytesPerSecond = if (bytesPerSecond > 0) {
                    ((bytesPerSecond * 3) + sampleBytesPerSecond) / 4
                } else {
                    sampleBytesPerSecond
                }
                entry.lastSampleBytes = transferredBytes
                entry.lastSampleElapsedMillis = now
            }
            val debugElapsedMillis = now - entry.lastDebugElapsedMillis
            if (BuildConfig.DEBUG && debugElapsedMillis >= DEBUG_SAMPLE_MILLIS &&
                transferredBytes >= entry.lastDebugBytes) {
                val intervalBytesPerSecond =
                    (transferredBytes - entry.lastDebugBytes) * 1000 / debugElapsedMillis
                val totalElapsedMillis = now - entry.transferStartedElapsedMillis!!
                val averageBytesPerSecond = if (totalElapsedMillis > 0) {
                    transferredBytes * 1000 / totalElapsedMillis
                } else {
                    0
                }
                val percent = if (totalBytes > 0) transferredBytes * 100 / totalBytes else -1
                Log.i(
                    BENCHMARK_TAG,
                    "SAMPLE id=$id direction=${entry.direction} percent=$percent " +
                        "bytes=$transferredBytes totalBytes=$totalBytes " +
                        "intervalBps=$intervalBytesPerSecond averageBps=$averageBytesPerSecond " +
                        "displayBps=$bytesPerSecond"
                )
                entry.lastDebugBytes = transferredBytes
                entry.lastDebugElapsedMillis = now
            }
            val remainingBytes = (totalBytes - transferredBytes).coerceAtLeast(0)
            val remainingSeconds = if (bytesPerSecond > 0) {
                (remainingBytes + bytesPerSecond - 1) / bytesPerSecond
            } else {
                null
            }
            entry.progress = entry.progress.copy(
                status = FileJobProgressStatus.TRANSFERRING,
                title = title,
                currentItem = currentItem,
                target = target,
                fileCount = fileCount,
                currentFileIndex = currentFileIndex,
                totalBytes = totalBytes,
                transferredBytes = transferredBytes,
                currentFileTotalBytes = currentFileTotalBytes,
                currentFileTransferredBytes = currentFileTransferredBytes,
                bytesPerSecond = bytesPerSecond,
                remainingSeconds = remainingSeconds
            )
            publishLocked()
        }
    }

    fun markCancelling(id: Int) {
        synchronized(entries) {
            val entry = entries[id] ?: return
            if (entry.progress.status.isFinished) {
                return
            }
            entry.progress = entry.progress.copy(status = FileJobProgressStatus.CANCELLING)
            publishLocked()
        }
    }

    fun finish(id: Int, status: FileJobProgressStatus, error: String? = null) {
        require(status.isFinished)
        synchronized(entries) {
            val entry = entries[id] ?: return
            entry.progress = entry.progress.copy(
                status = status,
                transferredBytes = if (status == FileJobProgressStatus.COMPLETED &&
                    entry.progress.totalBytes > 0) {
                    entry.progress.totalBytes
                } else {
                    entry.progress.transferredBytes
                },
                currentFileTransferredBytes =
                    if (status == FileJobProgressStatus.COMPLETED &&
                        entry.progress.currentFileTotalBytes > 0) {
                        entry.progress.currentFileTotalBytes
                    } else {
                        entry.progress.currentFileTransferredBytes
                    },
                remainingSeconds = if (status == FileJobProgressStatus.COMPLETED) 0 else null,
                error = error
            )
            if (BuildConfig.DEBUG) {
                val now = SystemClock.elapsedRealtime()
                val transferStarted = entry.transferStartedElapsedMillis
                val elapsedMillis = if (transferStarted != null) now - transferStarted else 0
                val averageBytesPerSecond = if (elapsedMillis > 0) {
                    entry.progress.transferredBytes * 1000 / elapsedMillis
                } else {
                    0
                }
                Log.i(
                    BENCHMARK_TAG,
                    "END id=$id direction=${entry.direction} status=$status " +
                        "bytes=${entry.progress.transferredBytes} " +
                        "totalBytes=${entry.progress.totalBytes} elapsedMs=$elapsedMillis " +
                        "averageBps=$averageBytesPerSecond error=${error ?: "none"}"
                )
            }
            publishLocked()
        }
    }

    fun dismiss(id: Int) {
        synchronized(entries) {
            val entry = entries[id] ?: return
            if (!entry.progress.status.isFinished) {
                return
            }
            entries.remove(id)
            publishLocked()
        }
    }

    private fun publishLocked() {
        mutableProgresses.postValue(entries.values.map { it.progress })
    }

    private const val MINIMUM_SAMPLE_MILLIS = 150L
    private const val DEBUG_SAMPLE_MILLIS = 2_000L
    private const val BENCHMARK_TAG = "FMPU.TransferBenchmark"
}
