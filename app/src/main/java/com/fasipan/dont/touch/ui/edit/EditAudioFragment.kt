package com.fasipan.dont.touch.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentEditAudioBinding
import com.fasipan.dont.touch.databinding.FragmentSettingBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.ui.home.AudioAdapter
import com.fasipan.dont.touch.utils.ex.clickSafe


class EditAudioFragment : BaseFragment() {

    private lateinit var binding: FragmentEditAudioBinding

    private val adapter  by lazy {
        AudioV2Adapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.rcyAudio.adapter = adapter
        LocalDataSource.getAllAudio().observe(viewLifecycleOwner) {
            adapter.setDataList(it.drop(1))
        }
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }

        binding.llApply.clickSafe {

        }


    }
}