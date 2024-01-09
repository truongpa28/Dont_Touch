package com.fasipan.dont.touch.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentEditAudioBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe


class EditAudioFragment : BaseFragment() {

    private lateinit var binding: FragmentEditAudioBinding

    private val adapter by lazy {
        AudioV2Adapter()
    }

    private var isEnableFlash = true
    private var isEnableVibrate = true
    private var isEnableSound = true
    private var volumeAudio = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAudioBinding.inflate(inflater, container, false)
        isEnableFlash = SharePreferenceUtils.isEnableFlashMode()
        isEnableVibrate = SharePreferenceUtils.isEnableVibrate()
        isEnableSound = SharePreferenceUtils.isEnableAudioSound()
        volumeAudio = SharePreferenceUtils.getVolumeAudioSound()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        showUi()
    }

    private fun showUi() {
        binding.swEnableFlash.setSwitchState(isEnableFlash)
        binding.swEnableVibrate.setSwitchState(isEnableVibrate)
        binding.swEnableSound.setSwitchState(isEnableSound)
        binding.sbVolume.progress = volumeAudio
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

        binding.sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    volumeAudio = progress
                    showUi()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}