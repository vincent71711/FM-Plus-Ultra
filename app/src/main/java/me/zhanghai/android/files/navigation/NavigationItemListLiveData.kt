/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.navigation

import androidx.lifecycle.MediatorLiveData
import me.zhanghai.android.files.settings.Settings
import me.zhanghai.android.files.storage.StorageVolumeListLiveData

object NavigationItemListLiveData : MediatorLiveData<List<NavigationItem?>>() {
    init {
        // Initialize value before we have any active observer.
        loadValue()
        addSource(Settings.STORAGES) { loadValue() }
        addSource(StorageVolumeListLiveData) { loadValue() }
        addSource(StandardDirectoriesLiveData) { loadValue() }
        addSource(Settings.BOOKMARK_DIRECTORIES) { loadValue() }
        addSource(Settings.RECENT_LOCATIONS) { loadValue() }
    }

    private fun loadValue() {
        value = navigationItems
    }
}

object HomeNavigationItemListLiveData : MediatorLiveData<List<NavigationItem>>() {
    init {
        loadValue()
        addSource(Settings.STORAGES) { loadValue() }
        addSource(Settings.HOME_SHORTCUT_STORAGE_IDS) { loadValue() }
        addSource(Settings.HOME_ITEM_ORDER) { loadValue() }
        addSource(StorageVolumeListLiveData) { loadValue() }
        addSource(StandardDirectoriesLiveData) { loadValue() }
        addSource(Settings.BOOKMARK_DIRECTORIES) { loadValue() }
    }

    private fun loadValue() {
        value = homeNavigationItems
    }
}
