/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import java8.nio.file.Path

// TODO: Make immutable?
class PasteState(
    var copy: Boolean = false,
    val files: FileItemSet = fileItemSetOf(),
    var sourceRoot: Path? = null
)
