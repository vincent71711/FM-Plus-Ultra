/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java8.nio.file.Path
import me.zhanghai.android.files.databinding.RecentActivityItemBinding
import me.zhanghai.android.files.ui.SimpleAdapter
import me.zhanghai.android.files.util.layoutInflater

class RecentActivityAdapter(
    private val listener: Listener
) : SimpleAdapter<Path, RecentActivityAdapter.ViewHolder>() {
    override val hasStableIds: Boolean = true

    override fun getItemId(position: Int): Long = getItem(position).toUri().hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(RecentActivityItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = getItem(position)
        val binding = holder.binding
        binding.root.setOnClickListener { listener.openRecentLocation(path) }
        binding.titleText.text = path.name.takeIf { it.isNotEmpty() }
            ?: path.toUserFriendlyString()
        binding.pathText.text = path.parent?.toUserFriendlyString()
    }

    class ViewHolder(val binding: RecentActivityItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun interface Listener {
        fun openRecentLocation(path: Path)
    }
}
