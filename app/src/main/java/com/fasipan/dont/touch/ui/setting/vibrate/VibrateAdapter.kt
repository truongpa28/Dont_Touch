package com.fasipan.dont.touch.ui.setting.vibrate

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemFlashBinding
import com.fasipan.dont.touch.databinding.ItemVibrateBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.showOrGone

class VibrateAdapter : BaseAdapterRecyclerView<Int, ItemVibrateBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVibrateBinding {
        return ItemVibrateBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemVibrateBinding, item: Int, position: Int) {
        val context = binding.root.context
        val choose = SharePreferenceUtils.getVibrateRingtone() == position

        binding.imgStateSelect.showOrGone(choose)
        binding.viewBorder.showOrGone(choose)

        if (choose) {
            binding.imgRingTone.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.color_type_flash_1
                ), PorterDuff.Mode.MULTIPLY
            )
        } else {
            binding.imgRingTone.colorFilter = null
        }
        binding.txtName.isSelected = true
        binding.txtName.text = if (position == 0) {
            context.getString(R.string.defaut)
        } else {
            String.format("%s %02d", context.getString(R.string.defaut), position + 1)
        }

        binding.imgRingTone.setImageResource(item)
    }
}