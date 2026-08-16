/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.storage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.files.R
import me.zhanghai.android.files.databinding.RemoteStorageListFragmentBinding
import me.zhanghai.android.files.filelist.FileListActivity
import me.zhanghai.android.files.settings.Settings
import me.zhanghai.android.files.util.createIntent
import me.zhanghai.android.files.util.getDrawableByAttr
import me.zhanghai.android.files.util.startActivitySafe
import me.zhanghai.android.files.util.valueCompat

class RemoteStorageListFragment : Fragment(), RemoteStorageListAdapter.Listener {
    private lateinit var binding: RemoteStorageListFragmentBinding
    private lateinit var adapter: RemoteStorageListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        RemoteStorageListFragmentBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.window.statusBarColor = Color.BLACK
        WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            .isAppearanceLightStatusBars = false
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RemoteStorageListAdapter(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(activity, RecyclerView.VERTICAL).apply {
                setDrawable(activity.getDrawableByAttr(android.R.attr.listDivider))
            }
        )
        binding.fab.setOnClickListener {
            startActivitySafe(AddStorageDialogActivity::class.createIntent())
        }
        Settings.STORAGES.observe(viewLifecycleOwner) { updateStorages(it) }
        Settings.HOME_SHORTCUT_STORAGE_IDS.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateStorages(storages: List<Storage>) {
        val remoteStorages = storages.filter { it.isVisible && it.isRemote }
        binding.emptyView.isVisible = remoteStorages.isEmpty()
        adapter.replace(remoteStorages)
    }

    override fun openStorage(storage: Storage) {
        startActivitySafe(FileListActivity.createViewIntent(storage.path!!))
    }

    override fun editStorage(storage: Storage) {
        startActivitySafe(storage.createEditIntent())
    }

    override fun isPinned(storage: Storage): Boolean =
        storage.id.toString() in Settings.HOME_SHORTCUT_STORAGE_IDS.valueCompat

    override fun setPinned(storage: Storage, pinned: Boolean) {
        val ids = Settings.HOME_SHORTCUT_STORAGE_IDS.valueCompat.toMutableSet()
        if (pinned) ids += storage.id.toString() else ids -= storage.id.toString()
        Settings.HOME_SHORTCUT_STORAGE_IDS.putValue(ids)
        Toast.makeText(
            requireContext(),
            if (pinned) R.string.remote_storage_pinned else R.string.remote_storage_unpinned,
            Toast.LENGTH_SHORT
        ).show()
    }
}
