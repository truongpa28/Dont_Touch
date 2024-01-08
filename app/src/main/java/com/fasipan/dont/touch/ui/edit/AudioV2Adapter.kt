package com.fasipan.dont.touch.ui.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemAudioV2Binding
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.showOrGone

class AudioV2Adapter : BaseAdapterRecyclerView<AudioEntity, ItemAudioV2Binding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemAudioV2Binding {
        return ItemAudioV2Binding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemAudioV2Binding, item: AudioEntity, position: Int) {
        val context = binding.root.context

        Glide.with(context).load(item.icon).into(binding.imgAvatar)

        binding.viewChoose.showOrGone(position == SharePreferenceUtils.getPositionAudioChoose() - 1)

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

        showNen(binding.viewBackground, position)
    }

    private fun showNen(view: ImageView, position: Int) {

        when (position % 4) {
            1 -> {
                view.setImageResource(R.drawable.bg_color_item_audio_2)
            }

            2 -> {
                view.setImageResource(R.drawable.bg_color_item_audio_3)
            }

            3 -> {
                view.setImageResource(R.drawable.bg_color_item_audio_4)
            }

            else -> {
                view.setImageResource(R.drawable.bg_color_item_audio_1)
            }
        }
    }
}