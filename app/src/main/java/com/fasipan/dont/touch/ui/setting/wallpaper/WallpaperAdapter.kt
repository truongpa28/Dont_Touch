package com.fasipan.dont.touch.ui.setting.wallpaper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseAdapterRecyclerView
import com.fasipan.dont.touch.databinding.ItemFlashBinding
import com.fasipan.dont.touch.databinding.ItemWallpaperBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.WallpaperUtils
import com.fasipan.dont.touch.utils.ex.loadGlide
import com.fasipan.dont.touch.utils.ex.showOrGone

class WallpaperAdapter: BaseAdapterRecyclerView<Int, ItemWallpaperBinding>() {


    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemWallpaperBinding {
        return ItemWallpaperBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemWallpaperBinding, item: Int, position: Int) {
        binding.imgWallpaper.loadGlide(item)
    }
}