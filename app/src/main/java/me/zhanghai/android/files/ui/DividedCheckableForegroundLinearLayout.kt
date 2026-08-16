/*
 * Copyright (c) 2026 Vincent Frosceno
 *
 * This file is part of FM Plus Ultra, a GPLv3 derivative of Material Files.
 */

package me.zhanghai.android.files.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet

/** Draws the list divider as part of the row so legacy row animations transform both together. */
class DividedCheckableForegroundLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CheckableForegroundLinearLayout(context, attrs, defStyleAttr) {
    private val divider: Drawable? = context.obtainStyledAttributes(
        intArrayOf(android.R.attr.listDivider)
    ).let { attributes ->
        try {
            attributes.getDrawable(0)
        } finally {
            attributes.recycle()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        divider?.let {
            val dividerHeight = it.intrinsicHeight.coerceAtLeast(1)
            it.setBounds(0, height - dividerHeight, width, height)
            it.draw(canvas)
        }
    }
}
