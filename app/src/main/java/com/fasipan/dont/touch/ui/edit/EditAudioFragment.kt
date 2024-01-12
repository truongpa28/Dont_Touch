package com.fasipan.dont.touch.ui.edit

import android.content.Context
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.custom.OnSwitchStateChangeListener
import com.fasipan.dont.touch.databinding.FragmentEditAudioBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.loadGlide
import com.fasipan.dont.touch.utils.ex.showOrGone


class EditAudioFragment : BaseFragment() {

    private lateinit var binding: FragmentEditAudioBinding

    private val adapter by lazy {
        AudioV2Adapter()
    }

    private var isEnableFlash = true
    private var isEnableVibrate = true
    private var isEnableSound = true
    private var volumeAudio = 100
    private var duration = 0

    private var isPlayingAudio = false

    private var positionChoose = 0

    private var audioEntity: AudioEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAudioBinding.inflate(inflater, container, false)
        isEnableFlash = SharePreferenceUtils.isEnableFlashMode()
        isEnableVibrate = SharePreferenceUtils.isEnableVibrate()
        isEnableSound = SharePreferenceUtils.isEnableAudioSound()
        volumeAudio = SharePreferenceUtils.getVolumeAudioSound()
        duration = SharePreferenceUtils.getPlayDuration()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        positionChoose = arguments?.getInt("pos", 0) ?: 0
        initView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        showUi()
        showDuration()
    }

    override fun onPause() {
        super.onPause()
        isPlayingAudio = false
        MediaPlayerUtils.stopMediaPlayer()
        binding.imgPlay.setImageResource(R.drawable.ic_play_edit_1)
    }

    private fun showUi() {
        binding.swEnableFlash.setSwitchState(isEnableFlash)
        binding.swEnableVibrate.setSwitchState(isEnableVibrate)
        binding.swEnableSound.setSwitchState(isEnableSound)
        binding.sbVolume.progress = volumeAudio
    }

    private fun showDuration() {
        binding.duration5s.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_1_o50
            )
        )
        binding.duration5s.background = null
        binding.duration15s.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_1_o50
            )
        )
        binding.duration15s.background = null
        binding.duration30s.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_1_o50
            )
        )
        binding.duration30s.background = null
        binding.duration1m.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_1_o50
            )
        )
        binding.duration1m.background = null
        when (duration) {
            1 -> {
                binding.duration15s.setBackgroundResource(R.drawable.bg_duration_edit)
                binding.duration15s.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_1
                    )
                )
            }

            2 -> {
                binding.duration30s.setBackgroundResource(R.drawable.bg_duration_edit)
                binding.duration30s.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_1
                    )
                )
            }

            3 -> {
                binding.duration1m.setBackgroundResource(R.drawable.bg_duration_edit)
                binding.duration1m.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_1
                    )
                )
            }

            else -> {
                binding.duration5s.setBackgroundResource(R.drawable.bg_duration_edit)
                binding.duration5s.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_1
                    )
                )
            }
        }
    }


    private fun initView() {
        binding.txtTitle.isSelected = true
        binding.rcyAudio.adapter = adapter
        LocalDataSource.getAllAudio().observe(viewLifecycleOwner) {
            audioEntity = it[positionChoose+1]
            adapter.setDataList(it.drop(1))
            binding.rcyAudio.scrollToPosition(positionChoose)
            showUiAudioChoose()
        }

        binding.txtName.isSelected = true
    }

    private fun showUiAudioChoose() {

        binding.viewVien.showOrGone(SharePreferenceUtils.getPositionAudioChoose() == (positionChoose+1))

        audioEntity?.let { item ->
            val nameAudio = if (item.isDefault) {
                try {
                    requireContext().getString(item.nameInt)
                } catch (_: Exception) {
                    ""
                }
            } else {
                item.nameString
            }
            binding.txtTitle.text = nameAudio
            binding.txtName.text = nameAudio
            binding.imgAvatar.loadGlide(item.icon)
            when (positionChoose % 4) {
                1 -> {
                    binding.imgBackground.setImageResource(R.drawable.bg_bg_info_audio_2)
                    binding.imgPlay.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_item_2_v2), PorterDuff.Mode.SRC_IN)
                }

                2 -> {
                    binding.imgBackground.setImageResource(R.drawable.bg_bg_info_audio_3)
                    binding.imgPlay.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_item_3_v2), PorterDuff.Mode.SRC_IN)
                }

                3 -> {
                    binding.imgBackground.setImageResource(R.drawable.bg_bg_info_audio_4)
                    binding.imgPlay.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_item_4_v2), PorterDuff.Mode.SRC_IN)
                }

                else -> {
                    binding.imgBackground.setImageResource(R.drawable.bg_bg_info_audio_1)
                    binding.imgPlay.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_item_1_v2), PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }

        binding.sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    volumeAudio = progress
                    showUi()
                    val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
                    val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val volume = maxVolume * (volumeAudio) / 100
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        volume,
                        0
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.swEnableFlash.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                isEnableFlash = isOn
                showUi()
            }
        })

        binding.swEnableVibrate.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                isEnableVibrate = isOn
                showUi()
            }
        })

        binding.swEnableSound.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                isEnableSound = isOn
                showUi()
            }
        })

        binding.duration5s.clickSafe {
            duration = 0
            showDuration()
        }

        binding.duration15s.clickSafe {
            duration = 1
            showDuration()
        }

        binding.duration30s.clickSafe {
            duration = 2
            showDuration()
        }

        binding.duration1m.clickSafe {
            duration = 3
            showDuration()
        }

        adapter.setOnClickItem { item, position ->
            MediaPlayerUtils.stopMediaPlayer()
            binding.imgPlay.setImageResource(R.drawable.ic_play_edit_1)
            isPlayingAudio = false
            audioEntity = item
            positionChoose = position
            showUiAudioChoose()
        }

        binding.imgPlay.clickSafe {
            if (isPlayingAudio) {
                isPlayingAudio = false
                MediaPlayerUtils.stopMediaPlayer()
                binding.imgPlay.setImageResource(R.drawable.ic_play_edit_1)
            } else {
                isPlayingAudio = true
                binding.imgPlay.setImageResource(R.drawable.ic_play_edit_2)
                audioEntity?.let { audio ->
                    val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
                    val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val volume = maxVolume * (volumeAudio) / 100
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        volume,
                        0
                    )
                    MediaPlayerUtils.playAudioSoundRecord(requireContext(), audio.sound) {
                        isPlayingAudio = false
                        binding.imgPlay.setImageResource(R.drawable.ic_play_edit_1)
                    }
                }
            }
        }

        binding.llApply.clickSafe {
            SharePreferenceUtils.setEnableFlashMode(isEnableFlash)
            SharePreferenceUtils.setEnableVibrate(isEnableVibrate)
            SharePreferenceUtils.setEnableAudioSound(isEnableSound)
            SharePreferenceUtils.setVolumeAudioSound(volumeAudio)
            SharePreferenceUtils.setPlayDuration(duration)
            SharePreferenceUtils.setPositionAudioChoose(positionChoose+1)
            audioEntity?.let { audio ->
                SharePreferenceUtils.setAudioWaring(audio.sound)
            }
            findNavController().popBackStack()
        }
    }
}