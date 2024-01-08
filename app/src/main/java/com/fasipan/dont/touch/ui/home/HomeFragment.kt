package com.fasipan.dont.touch.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentHomeBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.setOnTouchScale


class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val adapter by lazy {
        AudioAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.txtTapToActive.isSelected = true
        binding.txtName.isSelected = true
        binding.rcyAudio.adapter = adapter
        LocalDataSource.getAllAudio().observe(viewLifecycleOwner) {
            adapter.setDataList(it)

            val itemChoose = it[SharePreferenceUtils.getPositionAudioChoose()]
            binding.txtName.text = if (itemChoose.isDefault) {
                try {
                    getString(itemChoose.nameInt)
                } catch (_: Exception) {
                    ""
                }
            } else {
                itemChoose.nameString
            }

            Glide.with(requireContext()).load(itemChoose.icon).into(binding.imgAvatar)
        }
    }

    private fun initListener() {
        binding.imgMenu.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_menuFragment)
        }
        binding.imgSetting.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
        binding.llClapToFind.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_clapToFindFragment)
        }
        binding.llUnplugCharged.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_unplugBatteryFragment)
        }
        binding.llFullCharged.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_fullBatteryFragment)
        }

        adapter.setOnClickItem { item, position ->
            if (position == 0) {
                findNavController().navigate(R.id.action_homeFragment_to_addAudioFragment)
            } else {
                item?.let {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_editAudioFragment,
                        bundleOf("pos" to position)
                    )
                }
            }
        }

        binding.txtTapToActive.setOnTouchScale({
            if (SharePreferenceUtils.isAppServiceEnable()) {
                SharePreferenceUtils.setAppServiceEnable(false)
            } else {
                SharePreferenceUtils.setAppServiceEnable(true)
            }
            showChoose()
        }, 0.9f)
    }

    private fun showChoose() {
        if (SharePreferenceUtils.isAppServiceEnable()) {
            binding.bgBottom.setImageResource(R.drawable.bg_choose_home_2)
            binding.txtTapToActive.setBackgroundResource(R.drawable.bg_btn_tap_to_deactive)
            binding.txtTapToActive.text = getText(R.string.tap_to_deactive)
        } else {
            binding.bgBottom.setImageResource(R.drawable.bg_choose_home_1)
            binding.txtTapToActive.setBackgroundResource(R.drawable.bg_btn_tap_to_active)
            binding.txtTapToActive.text = getText(R.string.tap_to_active)
        }
    }

    override fun onResume() {
        super.onResume()
        showChoose()
        if (SharePreferenceUtils.isEnableFullPin()) {
            binding.llFullCharged.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llFullCharged.background = null
        }
        if (SharePreferenceUtils.isEnableUnplugPin()) {
            binding.llUnplugCharged.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llUnplugCharged.background = null
        }
        if (SharePreferenceUtils.isEnableClapToFind()) {
            binding.llClapToFind.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llClapToFind.background = null
        }
    }

    override fun onPause() {
        super.onPause()
        MediaPlayerUtils.stopMediaPlayer()
    }


    private var isClickBack = false
    override fun onBack() {
        if (isClickBack) {
            activity?.finish()
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.click_back),
                Toast.LENGTH_SHORT
            ).show()
            isClickBack = true
            Handler(Looper.getMainLooper()).postDelayed({
                isClickBack = false
            }, 1000L)
        }
    }
}