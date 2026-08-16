/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.zhanghai.android.files.R
import me.zhanghai.android.files.databinding.HomeDashboardItemBinding
import me.zhanghai.android.files.navigation.NavigationItem
import me.zhanghai.android.files.ui.SimpleAdapter
import me.zhanghai.android.files.util.layoutInflater

class HomeDashboardAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val listener: NavigationItem.Listener
) : SimpleAdapter<NavigationItem, HomeDashboardAdapter.ViewHolder>() {
    private val subtitleCache = mutableMapOf<Long, String?>()
    var onStartDrag: ((ViewHolder) -> Unit)? = null

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
        holder.boundItemId = item.id
        binding.root.setOnClickListener(if (isEditing) null else View.OnClickListener {
            item.onHomeClick(listener)
        })
        binding.root.setOnLongClickListener(if (isEditing) null else View.OnLongClickListener {
            item.onLongClick(listener)
        })
        binding.root.setOnTouchListener(if (isEditing) View.OnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                onStartDrag?.invoke(holder)
            }
            false
        } else null)
        holder.setEditing(isEditing, item.id)
        binding.iconImage.setImageDrawable(item.getIcon(binding.iconImage.context))
        val tintRes = ICON_TINTS[Math.floorMod(item.id.hashCode(), ICON_TINTS.size)]
        binding.iconImage.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(binding.iconImage.context, tintRes)
        )
        binding.titleText.text = item.getTitle(binding.titleText.context)
        holder.subtitleJob?.cancel()
        val context = binding.subtitleText.context
        binding.subtitleText.text = if (subtitleCache.containsKey(item.id)) {
            subtitleCache[item.id]
        } else {
            item.getSubtitle(context)
        }
        holder.subtitleJob = lifecycleOwner.lifecycleScope.launch {
            val subtitle = item.getHomeSubtitle(context)
            subtitleCache[item.id] = subtitle
            if (holder.boundItemId == item.id) {
                binding.subtitleText.text = subtitle
            }
        }
    }

    fun refreshSubtitles() {
        subtitleCache.clear()
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.subtitleJob?.cancel()
        holder.subtitleJob = null
        holder.boundItemId = RecyclerView.NO_ID
        holder.setEditing(false, 0L)
        super.onViewRecycled(holder)
    }

    class ViewHolder(val binding: HomeDashboardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var editAnimator: ObjectAnimator? = null
        var boundItemId: Long = RecyclerView.NO_ID
        var subtitleJob: Job? = null

        fun setEditing(editing: Boolean, itemId: Long) {
            editAnimator?.cancel()
            editAnimator = null
            binding.root.rotation = 0f
            if (!editing) {
                return
            }
            val phase = if (itemId and 1L == 0L) 1f else -1f
            binding.root.rotation = -EDIT_ROTATION_DEGREES * phase
            editAnimator = ObjectAnimator.ofFloat(
                binding.root,
                View.ROTATION,
                -EDIT_ROTATION_DEGREES * phase,
                EDIT_ROTATION_DEGREES * phase
            ).apply {
                duration = EDIT_ROTATION_DURATION_MILLIS
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = LinearInterpolator()
                startDelay = Math.floorMod(itemId, EDIT_ROTATION_DURATION_MILLIS)
                start()
            }
        }
    }

    companion object {
        private const val EDIT_ROTATION_DEGREES = 1.2f
        private const val EDIT_ROTATION_DURATION_MILLIS = 130L

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
