package com.fasipan.dont.touch.ui.setting.wallpaper

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentDetailWallpaperBinding
import com.fasipan.dont.touch.databinding.FragmentWallpaperBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.WallpaperUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.loadGlide
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class DetailWallpaperFragment : BaseFragment() {

    private lateinit var binding: FragmentDetailWallpaperBinding

    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailWallpaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        position = arguments?.getInt("position")?:0
        binding.imgWallpaper.loadGlide(WallpaperUtils.listWallpaper[position])
    }

    private var isSetting = false

    private fun initListener() {
        binding.imgBack.clickSafe {
            onBack()
        }
        binding.txtDone.setOnTouchScale({
            if (!isSetting) {
                isSetting = true
                setDeviceWallpaper(WallpaperUtils.listWallpaper[position])
            }
        }, 0.9f)
    }

    private fun setDeviceWallpaper(data : Int) {
        binding.prgLoading.show()
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeResource(resources, data)
                wallpaperManager.setBitmap(bitmap)
                requireActivity().runOnUiThread {
                    isSetting = false
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.wallpaper_has_been_set_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.prgLoading.gone()
                    onBack()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                isSetting = false
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.setting_wallpaper_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.prgLoading.gone()
                }
            }
        }
    }
}