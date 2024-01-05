package com.fasipan.dont.touch.ui.setting.wallpaper

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentWallpaperBinding
import com.fasipan.dont.touch.ui.setting.vibrate.VibrateAdapter
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.VibrateRingtoneUtils
import com.fasipan.dont.touch.utils.data.WallpaperUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.initVibrator
import com.fasipan.dont.touch.utils.ex.startVibration


class WallpaperFragment : BaseFragment() {

    private lateinit var binding: FragmentWallpaperBinding

    private val adapter by lazy {
        WallpaperAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWallpaperBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        adapter.setDataList(WallpaperUtils.listWallpaper)
        binding.rcyWallpaper.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }
        adapter.setOnClickItem { _, position ->
            findNavController().navigate(R.id.action_wallpaperFragment_to_detailWallpaperFragment, bundleOf("position" to position))
        }
    }
}