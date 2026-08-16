/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.ui

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.OnWindowInsetChangedAppBarLayout

open class FitsSystemWindowsAppBarLayout : OnWindowInsetChangedAppBarLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    init {
        fitsSystemWindows = true
    }

    override fun onWindowInsetChanged(insets: WindowInsetsCompat): WindowInsetsCompat {
        // This bar sits below the status-bar cutout. Applying its horizontal safe inset shifts the
        // whole header out of alignment and leaves a visible gap on a rotated cover display.
        updatePadding(left = 0, right = 0)
        return super.onWindowInsetChanged(insets)
    }
}
