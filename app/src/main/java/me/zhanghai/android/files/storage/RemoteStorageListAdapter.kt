/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.storage

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.files.R
import me.zhanghai.android.files.databinding.StorageItemBinding
import me.zhanghai.android.files.ui.SimpleAdapter
import me.zhanghai.android.files.util.layoutInflater

class RemoteStorageListAdapter(
    private val listener: Listener
) : SimpleAdapter<Storage, RemoteStorageListAdapter.ViewHolder>() {
    override val hasStableIds: Boolean = true

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(StorageItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storage = getItem(position)
        val binding = holder.binding
        binding.root.setOnClickListener { listener.openStorage(storage) }
        val showMenu = View.OnClickListener { showStorageMenu(storage, binding.root) }
        binding.root.setOnLongClickListener {
            showStorageMenu(storage, binding.root)
            true
        }
        binding.dragHandleView.setOnClickListener(showMenu)
        binding.dragHandleView.setImageResource(
            if (listener.isPinned(storage)) R.drawable.home_icon_control_normal_24dp
            else R.drawable.more_vertical_icon_white_24dp
        )
        binding.iconImage.setImageResource(storage.iconRes)
        binding.nameText.isActivated = true
        binding.nameText.text = storage.getName(binding.nameText.context)
        binding.descriptionText.text = storage.description
    }

    private fun showStorageMenu(storage: Storage, anchor: View) {
        val isPinned = listener.isPinned(storage)
        PopupMenu(anchor.context, anchor).apply {
            menu.add(
                if (isPinned) R.string.remote_storage_unpin else R.string.remote_storage_pin
            ).setOnMenuItemClickListener {
                listener.setPinned(storage, !isPinned)
                true
            }
            menu.add(R.string.edit).setOnMenuItemClickListener {
                listener.editStorage(storage)
                true
            }
            show()
        }
    }

    class ViewHolder(val binding: StorageItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        fun openStorage(storage: Storage)
        fun editStorage(storage: Storage)
        fun isPinned(storage: Storage): Boolean
        fun setPinned(storage: Storage, pinned: Boolean)
    }
}
