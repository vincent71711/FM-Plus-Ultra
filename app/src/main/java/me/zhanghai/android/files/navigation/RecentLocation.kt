/*
 * Copyright (c) 2026 Vincent Frosceno
 *
 * This file is part of File Manager Plus Ultra, a GPLv3 derivative of Material Files.
 */

package me.zhanghai.android.files.navigation

import android.os.Parcelable
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import me.zhanghai.android.files.settings.Settings
import me.zhanghai.android.files.util.ParcelableParceler
import me.zhanghai.android.files.util.valueCompat

@Parcelize
data class RecentLocation(
    val path: @WriteWith<ParcelableParceler> Path
) : Parcelable

object RecentLocations {
    fun record(path: Path) {
        if (isNavigationRootPath(path)) {
            return
        }
        val locations = Settings.RECENT_LOCATIONS.valueCompat.toMutableList()
        locations.removeAll { it.path == path || isNavigationRootPath(it.path) }
        locations.add(0, RecentLocation(path))
        if (locations.size > RECENT_LOCATION_COUNT_MAX) {
            locations.subList(RECENT_LOCATION_COUNT_MAX, locations.size).clear()
        }
        Settings.RECENT_LOCATIONS.putValue(locations)
    }

    private const val RECENT_LOCATION_COUNT_MAX = 5
}
