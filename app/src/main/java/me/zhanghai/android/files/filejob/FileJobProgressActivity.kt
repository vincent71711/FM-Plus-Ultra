/*
 * Copyright (c) 2026 Vincent Frosceno
 *
 * This file is part of the FM Plus Ultra modifications.
 */

package me.zhanghai.android.files.filejob

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.zhanghai.android.files.R
import me.zhanghai.android.files.app.AppActivity
import me.zhanghai.android.files.databinding.FileJobProgressActivityBinding
import me.zhanghai.android.files.util.layoutInflater

class FileJobProgressActivity : AppActivity() {
    private val jobId: Int
        get() = intent.getIntExtra(EXTRA_JOB_ID, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure AppCompat has installed its content view before adding the dialog fragment.
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<FileJobProgressDialogFragment>(
                    FileJobProgressDialogFragment::class.java.name,
                    args = Bundle().apply {
                        putInt(FileJobProgressDialogFragment.ARG_JOB_ID, jobId)
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_JOB_ID = "jobId"

        fun createIntent(jobId: Int, context: Context): Intent =
            Intent(context, FileJobProgressActivity::class.java)
                .putExtra(EXTRA_JOB_ID, jobId)

        fun show(jobId: Int, context: Context) {
            val intent = createIntent(jobId, context)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            FileJobProgressStore.setDialogVisible(jobId, true)
            context.startActivity(intent)
        }
    }
}

class FileJobProgressDialogFragment : AppCompatDialogFragment() {
    private lateinit var binding: FileJobProgressActivityBinding
    private var progress: FileJobProgress? = null
    private var hasPerformedCompletionHaptic = false

    private val jobId: Int
        get() = requireArguments().getInt(ARG_JOB_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasPerformedCompletionHaptic =
            savedInstanceState?.getBoolean(STATE_COMPLETION_HAPTIC) ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FileJobProgressActivityBinding.inflate(requireContext().layoutInflater)
        binding.actionButton.setOnClickListener {
            val progress = progress
            if (progress == null || progress.status.isFinished) {
                progress?.let { FileJobProgressStore.dismiss(it.id) }
                dismiss()
            } else {
                FileJobService.cancelJob(progress.id)
            }
        }
        FileJobProgressStore.progresses.observe(this) { progresses ->
            val progress = progresses.firstOrNull { it.id == jobId }
            this.progress = progress
            render(progress)
            if (progress?.status == FileJobProgressStatus.COMPLETED &&
                !hasPerformedCompletionHaptic) {
                hasPerformedCompletionHaptic = true
                binding.root.post {
                    binding.root.performHapticFeedback(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            HapticFeedbackConstants.CONFIRM
                        } else {
                            HapticFeedbackConstants.LONG_PRESS
                        }
                    )
                }
            }
        }
        render(null)
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setTitle(R.string.file_job_progress_title)
            .setView(binding.root)
            .create()
    }

    private fun render(progress: FileJobProgress?) {
        if (progress == null) {
            binding.statusText.setText(R.string.file_job_progress_no_active)
            binding.titleText.setText(R.string.file_job_progress_title)
            binding.pathText.isVisible = false
            binding.progressIndicator.isIndeterminate = false
            binding.progressIndicator.setProgressCompat(0, false)
            binding.percentText.isVisible = false
            binding.sizeText.isVisible = false
            binding.speedText.isVisible = false
            binding.remainingText.isVisible = false
            binding.currentFileProgressLabel.isVisible = false
            binding.currentFileProgressIndicator.isVisible = false
            binding.currentFileProgressText.isVisible = false
            binding.overallProgressLabel.isVisible = false
            binding.actionButton.setText(android.R.string.ok)
            return
        }

        binding.statusText.text = getStatusText(progress.status)
        binding.titleText.text = progress.title
        binding.pathText.text = listOfNotNull(
            progress.sourceLocation, progress.targetLocation
        )
            .joinToString("  →  ")
        binding.pathText.isVisible = progress.sourceLocation != null ||
            progress.targetLocation != null

        val showCurrentFileProgress = progress.fileCount > 1
        binding.currentFileProgressLabel.text = getString(
            R.string.file_job_progress_current_file,
            progress.currentFileIndex,
            progress.fileCount
        )
        binding.currentFileProgressLabel.isVisible = showCurrentFileProgress
        val currentFilePercent = progress.currentFilePercent
        binding.currentFileProgressIndicator.isVisible = showCurrentFileProgress
        binding.currentFileProgressIndicator.isIndeterminate =
            showCurrentFileProgress && currentFilePercent == null && !progress.status.isFinished
        if (currentFilePercent != null) {
            binding.currentFileProgressIndicator.setProgressCompat(currentFilePercent, true)
        } else if (!binding.currentFileProgressIndicator.isIndeterminate) {
            binding.currentFileProgressIndicator.setProgressCompat(0, false)
        }
        binding.currentFileProgressText.text = currentFilePercent?.let {
            getString(
                R.string.file_job_progress_current_file_size,
                it,
                Formatter.formatFileSize(
                    requireContext(),
                    progress.currentFileTransferredBytes.coerceIn(
                        0, progress.currentFileTotalBytes
                    )
                ),
                Formatter.formatFileSize(requireContext(), progress.currentFileTotalBytes)
            )
        }
        binding.currentFileProgressText.isVisible =
            showCurrentFileProgress && currentFilePercent != null
        binding.overallProgressLabel.isVisible = true

        val percent = progress.percent
        val indeterminate = percent == null && !progress.status.isFinished
        binding.progressIndicator.isIndeterminate = indeterminate
        if (!indeterminate) {
            binding.progressIndicator.setProgressCompat(percent ?: 0, true)
        }
        binding.percentText.text = getString(R.string.file_job_progress_percent, percent ?: 0)
        binding.percentText.isVisible = percent != null

        binding.sizeText.text = getString(
            R.string.file_job_progress_size,
            Formatter.formatFileSize(requireContext(), progress.transferredBytes),
            Formatter.formatFileSize(requireContext(), progress.totalBytes)
        )
        binding.sizeText.isVisible = progress.totalBytes > 0

        binding.speedText.text = getString(
            R.string.file_job_progress_speed,
            Formatter.formatFileSize(requireContext(), progress.bytesPerSecond)
        )
        binding.speedText.isVisible = progress.bytesPerSecond > 0 && !progress.status.isFinished

        binding.remainingText.text = progress.remainingSeconds?.let {
            getString(R.string.file_job_progress_remaining, formatDuration(it))
        }
        binding.remainingText.isVisible = progress.remainingSeconds != null &&
            !progress.status.isFinished

        binding.actionButton.text = if (progress.status.isFinished) {
            getString(android.R.string.ok)
        } else {
            getString(android.R.string.cancel)
        }
        binding.actionButton.isEnabled = progress.status != FileJobProgressStatus.CANCELLING
    }

    private fun getStatusText(status: FileJobProgressStatus): String = getString(
        when (status) {
            FileJobProgressStatus.PREPARING -> R.string.file_job_progress_preparing
            FileJobProgressStatus.SCANNING -> R.string.file_job_progress_scanning
            FileJobProgressStatus.TRANSFERRING -> R.string.file_job_progress_transferring
            FileJobProgressStatus.CANCELLING -> R.string.file_job_progress_cancelling
            FileJobProgressStatus.COMPLETED -> R.string.file_job_progress_completed
            FileJobProgressStatus.CANCELLED -> R.string.file_job_progress_cancelled
            FileJobProgressStatus.FAILED -> R.string.file_job_progress_failed
        }
    )

    private fun formatDuration(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return if (minutes > 0) {
            getString(R.string.file_job_progress_duration_minutes, minutes, remainingSeconds)
        } else {
            getString(R.string.file_job_progress_duration_seconds, remainingSeconds)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        FileJobProgressStore.setDialogVisible(jobId, false)
        activity?.finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_COMPLETION_HAPTIC, hasPerformedCompletionHaptic)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val ARG_JOB_ID = "jobId"
        private const val STATE_COMPLETION_HAPTIC = "completionHaptic"
    }
}
