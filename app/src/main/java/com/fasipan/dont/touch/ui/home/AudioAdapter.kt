package com.fasipan.dont.touch.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemAudioBinding
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.loadGlide
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.showOrGone

class AudioAdapter : BaseAdapterRecyclerView<AudioEntity, ItemAudioBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemAudioBinding {
        return ItemAudioBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemAudioBinding, item: AudioEntity, position: Int) {
        val context = binding.root.context

        binding.imgAvatar.loadGlide(item.icon)

        binding.viewChoose.showOrGone(position == SharePreferenceUtils.getPositionAudioChoose())

        binding.txtName.text = if (item.isDefault) {
            try {
                context.getString(item.nameInt)
            } catch (_: Exception) {
                ""
            }
        } else {
            item.nameString
        }

        binding.txtName.isSelected = true

        binding.imgDelete.showOrGone(!item.isDefault)

        showNen(binding.viewBackground, position)

        binding.viewAll.setOnTouchScale({
            if (position == RecyclerView.NO_POSITION) {
                return@setOnTouchScale
            }
            setOnClickAudio?.invoke(dataList.getOrNull(position), position)
        }, 0.9f)

        binding.imgDelete.setOnTouchScale({
            if (position == RecyclerView.NO_POSITION) {
                return@setOnTouchScale
            }
            setOnClickDeleteAudio?.invoke(dataList.getOrNull(position), position)
        }, 0.9f)
    }

    private var setOnClickAudio: ((item: AudioEntity?, position: Int) -> Unit)? = null
    fun setOnClickAudio(listener: ((item: AudioEntity?, position: Int) -> Unit)? = null) {
        setOnClickAudio = listener
    }

    private var setOnClickDeleteAudio: ((item: AudioEntity?, position: Int) -> Unit)? = null
    fun setOnClickDeleteAudio(listener: ((item: AudioEntity?, position: Int) -> Unit)? = null) {
        setOnClickDeleteAudio = listener
    }

    private fun showNen(view: ImageView, position: Int) {
        if (position == 0) {
            view.setImageResource(0)
        } else {
            when (position % 4) {
                1 -> {
                    view.setImageResource(R.drawable.bg_color_item_audio_1)
                }

                2 -> {
                    view.setImageResource(R.drawable.bg_color_item_audio_2)
                }

                3 -> {
                    view.setImageResource(R.drawable.bg_color_item_audio_3)
                }

                else -> {
                    view.setImageResource(R.drawable.bg_color_item_audio_4)
                }
            }
        }
    }
}