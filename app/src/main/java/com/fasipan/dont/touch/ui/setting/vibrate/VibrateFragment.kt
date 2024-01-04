package com.fasipan.dont.touch.ui.setting.vibrate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentVibrateBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.VibrateRingtoneUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.initVibrator
import com.fasipan.dont.touch.utils.ex.startVibration
import com.fasipan.dont.touch.utils.ex.turnOffVibration

class VibrateFragment : BaseFragment() {

    private lateinit var binding: FragmentVibrateBinding

    private val adapter by lazy {
        VibrateAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVibrateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        adapter.setDataList(VibrateRingtoneUtils.listIcon)
        binding.rcyVibrate.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }
        adapter.setOnClickItem { _, position ->
            SharePreferenceUtils.setVibrateRingtone(position)
            adapter.notifyDataSetChanged()
            initVibrator(requireContext())
            startVibration(-1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        turnOffVibration()
    }

    override fun onPause() {
        super.onPause()
        turnOffVibration()
    }
}