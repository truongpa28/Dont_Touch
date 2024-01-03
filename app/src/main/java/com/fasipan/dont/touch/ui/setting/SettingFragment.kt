package com.fasipan.dont.touch.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentSettingBinding
import com.fasipan.dont.touch.utils.ex.clickSafe

class SettingFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
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
        binding.imgBack.clickSafe { onBack() }
        binding.imgHelp.clickSafe {
            //findNavController().navigate(R.id.ac)
        }
    }
}