package com.fasipan.dont.touch.ui.setting.flash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemFlashBinding
import com.fasipan.dont.touch.model.SpeedFlashModel
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.showOrGone

class FlashAdapter: BaseAdapterRecyclerView<SpeedFlashModel, ItemFlashBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemFlashBinding {
        return ItemFlashBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemFlashBinding, item: SpeedFlashModel, position: Int) {
        val context = binding.root.context
        val choose = SharePreferenceUtils.getTypeFlash() == position

        binding.imgStateSelect.showOrGone(choose)
        binding.viewBorder.showOrGone(choose)

        if (choose) {
            binding.imgRingTone.setColorFilter(ContextCompat.getColor(context, R.color.color_type_flash_1))
        } else {
            binding.imgRingTone.setColorFilter(ContextCompat.getColor(context, R.color.color_type_flash))
        }

        binding.imgRingTone.setImageResource(item.icon)
    }
}