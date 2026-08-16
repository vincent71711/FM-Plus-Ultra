/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.files.R
import me.zhanghai.android.files.databinding.HomeDashboardItemBinding
import me.zhanghai.android.files.navigation.NavigationItem
import me.zhanghai.android.files.ui.SimpleAdapter
import me.zhanghai.android.files.util.layoutInflater

class HomeDashboardAdapter(
    private val listener: NavigationItem.Listener
) : SimpleAdapter<NavigationItem, HomeDashboardAdapter.ViewHolder>() {
    var isEditing: Boolean = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            notifyDataSetChanged()
        }

    override val hasStableIds: Boolean = true

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(HomeDashboardItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        binding.root.setOnClickListener(if (isEditing) null else View.OnClickListener {
            item.onHomeClick(listener)
        })
        binding.root.setOnLongClickListener(if (isEditing) null else View.OnLongClickListener {
            item.onLongClick(listener)
        })
        binding.dragHandle.isVisible = isEditing
        binding.iconImage.setImageDrawable(item.getIcon(binding.iconImage.context))
        val tintRes = ICON_TINTS[Math.floorMod(item.id.hashCode(), ICON_TINTS.size)]
        binding.iconImage.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(binding.iconImage.context, tintRes)
        )
        binding.titleText.text = item.getTitle(binding.titleText.context)
        binding.subtitleText.text = item.getSubtitle(binding.subtitleText.context)
    }

    class ViewHolder(val binding: HomeDashboardItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val ICON_TINTS = intArrayOf(
            R.color.file_icon_blue,
            R.color.file_icon_green,
            R.color.file_icon_orange,
            R.color.file_icon_purple,
            R.color.file_icon_cyan,
            R.color.file_icon_red
        )
    }
}
