package com.fasipan.dont.touch.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemAudioBinding
import com.fasipan.dont.touch.databinding.ItemFlashBinding
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.model.SpeedFlashModel
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.showOrGone

class AudioAdapter: BaseAdapterRecyclerView<AudioEntity, ItemAudioBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemAudioBinding {
        return ItemAudioBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemAudioBinding, item: AudioEntity, position: Int) {
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