package com.fasipan.dont.touch.ui.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemAudioBinding
import com.fasipan.dont.touch.databinding.ItemAudioV2Binding
import com.fasipan.dont.touch.databinding.ItemFlashBinding
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.model.SpeedFlashModel
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.showOrGone

class AudioV2Adapter: BaseAdapterRecyclerView<AudioEntity, ItemAudioV2Binding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemAudioV2Binding {
        return ItemAudioV2Binding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemAudioV2Binding, item: AudioEntity, position: Int) {
        val context = binding.root.context

        Glide.with(context).load(item.icon).into(binding.imgAvatar)

        binding.txtName.text = if (item.isDefault) {
            try {
                context.getString(item.nameInt)
            } catch (_: Exception) {
                ""
            }
        } else {
            item.nameString
        }
    }
}