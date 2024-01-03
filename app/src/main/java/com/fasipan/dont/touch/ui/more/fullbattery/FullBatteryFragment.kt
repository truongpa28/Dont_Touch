package com.fasipan.dont.touch.ui.more.fullbattery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentFullBatteryBinding

class FullBatteryFragment : BaseFragment() {

    private lateinit var binding: FragmentFullBatteryBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullBatteryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {

    }
}