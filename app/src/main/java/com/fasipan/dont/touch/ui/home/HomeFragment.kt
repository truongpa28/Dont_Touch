package com.fasipan.dont.touch.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentHomeBinding
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.data.DataAudioUtils
import com.fasipan.dont.touch.utils.ex.clickSafe


class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val adapter  by lazy {
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
        adapter.setDataList(DataAudioUtils.listDefault)
        binding.rcyAudio.adapter = adapter
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
                    findNavController().navigate(R.id.action_homeFragment_to_editAudioFragment,
                        bundleOf("pos" to position)
                    )
                }
            }
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