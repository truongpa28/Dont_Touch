package com.fasipan.dont.touch.ui.more.clap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentClapToFindBinding
import com.fasipan.dont.touch.utils.ex.clickSafe

class ClapToFindFragment : BaseFragment() {

    private lateinit var binding: FragmentClapToFindBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClapToFindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.imgBack.clickSafe { onBack() }
    }

    private fun initListener() {

    }
}