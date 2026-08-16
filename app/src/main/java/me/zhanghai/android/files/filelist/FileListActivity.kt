/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 * Modified 2026-08-16 for FM Plus Ultra.
 */

package me.zhanghai.android.files.filelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.commit
import java8.nio.file.Path
import me.zhanghai.android.files.R
import me.zhanghai.android.files.app.AppActivity
import me.zhanghai.android.files.file.MimeType
import me.zhanghai.android.files.util.createIntent
import me.zhanghai.android.files.util.extraPath
import me.zhanghai.android.files.util.putArgs

class FileListActivity : AppActivity() {
    private lateinit var fragment: FileListFragment
    private lateinit var exitGuardCallback: OnBackPressedCallback
    private var lastBackUptimeMillis = 0L
    private var exitConfirmationToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register before the fragment callbacks so navigation drawers, nested folders, selection
        // modes and other transient UI continue to consume Back first. This is reached only when
        // the launcher activity is already at its true top level.
        exitGuardCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                val now = SystemClock.elapsedRealtime()
                if (lastBackUptimeMillis != 0L &&
                    now - lastBackUptimeMillis <= EXIT_CONFIRMATION_WINDOW_MILLIS) {
                    exitConfirmationToast?.cancel()
                    exitConfirmationToast = null
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                } else {
                    lastBackUptimeMillis = now
                    exitConfirmationToast?.cancel()
                    exitConfirmationToast = Toast.makeText(
                        this@FileListActivity,
                        R.string.file_list_exit_confirmation,
                        Toast.LENGTH_SHORT
                    ).also { it.show() }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, exitGuardCallback)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            fragment = FileListFragment().putArgs(FileListFragment.Args(intent))
            supportFragmentManager.commit { add(android.R.id.content, fragment) }
        } else {
            fragment = supportFragmentManager.findFragmentById(android.R.id.content)
                as FileListFragment
        }
    }

    override fun onDestroy() {
        exitConfirmationToast?.cancel()
        exitConfirmationToast = null
        super.onDestroy()
    }

    override fun onKeyShortcut(keyCode: Int, event: KeyEvent): Boolean {
        if (fragment.onKeyShortcut(keyCode, event)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    fun setExitGuardEnabled(enabled: Boolean) {
        if (enabled && !exitGuardCallback.isEnabled) {
            lastBackUptimeMillis = 0L
        }
        exitGuardCallback.isEnabled = enabled
    }

    companion object {
        private const val EXIT_CONFIRMATION_WINDOW_MILLIS = 2500L

        fun createViewIntent(path: Path): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_VIEW)
                .apply { extraPath = path }
    }

    class OpenFileContract : ActivityResultContract<List<MimeType>, Path?>() {
        override fun createIntent(context: Context, input: List<MimeType>): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .setType(MimeType.ANY.value)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_MIME_TYPES, input.map { it.value }.toTypedArray())

        override fun parseResult(resultCode: Int, intent: Intent?): Path? =
            if (resultCode == RESULT_OK) intent?.extraPath else null
    }

    class CreateFileContract : ActivityResultContract<Triple<MimeType, String?, Path?>, Path?>() {
        override fun createIntent(
            context: Context,
            input: Triple<MimeType, String?, Path?>
        ): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_CREATE_DOCUMENT)
                .setType(input.first.value)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .apply {
                    input.second?.let { putExtra(Intent.EXTRA_TITLE, it) }
                    input.third?.let { extraPath = it }
                }

        override fun parseResult(resultCode: Int, intent: Intent?): Path? =
            if (resultCode == RESULT_OK) intent?.extraPath else null
    }

    class OpenDirectoryContract : ActivityResultContract<Path?, Path?>() {
        override fun createIntent(context: Context, input: Path?): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .apply { input?.let { extraPath = it } }

        override fun parseResult(resultCode: Int, intent: Intent?): Path? =
            if (resultCode == RESULT_OK) intent?.extraPath else null
    }
}
