package com.fasipan.dont.touch.ui.setting.vibrate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemFlashBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.showOrGone

class VibrateAdapter: BaseAdapterRecyclerView<Int, ItemFlashBinding>() {


    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemFlashBinding {
        return ItemFlashBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemFlashBinding, item: Int, position: Int) {
        val context = binding.root.context
        val choose = SharePreferenceUtils.getVibrateRingtone() == position

        binding.imgStateSelect.showOrGone(choose)
        binding.viewBorder.showOrGone(choose)

        if (choose) {
            binding.imgRingTone.setColorFilter(ContextCompat.getColor(context, R.color.color_type_flash_1))
        } else {
            binding.imgRingTone.setColorFilter(ContextCompat.getColor(context, R.color.color_type_flash))
        }

        binding.imgRingTone.setImageResource(item)
    }
}