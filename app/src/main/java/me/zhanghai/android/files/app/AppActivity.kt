/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 * Modified 2026-08-20 for FM Plus Ultra.
 */

package me.zhanghai.android.files.app

import android.graphics.Rect
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import me.zhanghai.android.files.theme.custom.CustomThemeHelper
import me.zhanghai.android.files.theme.night.NightModeHelper

abstract class AppActivity : AppCompatActivity() {
    private var isDelegateCreated = false

    private var hapticTapTarget: View? = null
    private var hapticTapDownX = 0f
    private var hapticTapDownY = 0f
    private var isHapticTapCanceled = false
    private val hapticHitRect = Rect()

    private val hapticTapTouchSlopSquared by lazy {
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop.toFloat()
        touchSlop * touchSlop
    }

    override fun getDelegate(): AppCompatDelegate {
        val delegate = super.getDelegate()

        if (!isDelegateCreated) {
            isDelegateCreated = true
            NightModeHelper.apply(this)
        }
        return delegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        CustomThemeHelper.apply(this)

        super.onCreate(savedInstanceState)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!super.onSupportNavigateUp()) {
            finish()
        }
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val decorView = window.decorView
        var performTapHaptic = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                hapticTapTarget = findClickableViewAt(decorView, event.rawX, event.rawY)
                hapticTapDownX = event.rawX
                hapticTapDownY = event.rawY
                isHapticTapCanceled = false
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - hapticTapDownX
                val deltaY = event.rawY - hapticTapDownY
                if (deltaX * deltaX + deltaY * deltaY > hapticTapTouchSlopSquared) {
                    isHapticTapCanceled = true
                }
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_CANCEL -> {
                isHapticTapCanceled = true
            }
            MotionEvent.ACTION_UP -> {
                val target = hapticTapTarget
                performTapHaptic = !isHapticTapCanceled && target != null &&
                    event.eventTime - event.downTime < ViewConfiguration.getLongPressTimeout() &&
                    target === findClickableViewAt(decorView, event.rawX, event.rawY)
            }
        }
        val handled = super.dispatchTouchEvent(event)
        if (handled && performTapHaptic) {
            decorView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
        if (event.actionMasked == MotionEvent.ACTION_UP ||
            event.actionMasked == MotionEvent.ACTION_CANCEL) {
            hapticTapTarget = null
        }
        return handled
    }

    private fun findClickableViewAt(view: View, rawX: Float, rawY: Float): View? {
        if (!view.isShown || !view.getGlobalVisibleRect(hapticHitRect) ||
            !hapticHitRect.contains(rawX.toInt(), rawY.toInt())) {
            return null
        }
        if (view is ViewGroup) {
            for (index in view.childCount - 1 downTo 0) {
                findClickableViewAt(view.getChildAt(index), rawX, rawY)?.let { return it }
            }
        }
        return view.takeIf { it.isClickable && it.isEnabled && it.isHapticFeedbackEnabled }
    }
}
