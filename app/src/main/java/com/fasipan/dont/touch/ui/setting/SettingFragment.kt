package com.fasipan.dont.touch.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.custom.OnSwitchStateChangeListener
import com.fasipan.dont.touch.databinding.FragmentSettingBinding
import com.fasipan.dont.touch.ui.dialog.DialogMoreHelp
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.setOnTouchScale

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

    override fun onResume() {
        super.onResume()
        showUi()
    }

    private fun showUi() {
        binding.swEnableLightUp.setSwitchState(SharePreferenceUtils.isEnableLightUpMode())
        binding.swEnableFlash.setSwitchState(SharePreferenceUtils.isEnableFlashMode())
        binding.swEnableVibrate.setSwitchState(SharePreferenceUtils.isEnableVibrate())
        binding.sbSensitivity.progress = SharePreferenceUtils.getSensitivity()
    }

    private fun initView() {
        binding.txtTitleMoreHelp.isSelected = true
        binding.btnSetNow.isSelected = true
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }
        binding.imgHelp.clickSafe {
            findNavController().navigate(R.id.action_settingFragment_to_howToUseFragment)
        }

        binding.llSelectFlash.clickSafe {
            findNavController().navigate(R.id.action_settingFragment_to_flashFragment)
        }

        binding.llSelectVibrate.clickSafe {
            findNavController().navigate(R.id.action_settingFragment_to_vibrateFragment)
        }

        binding.llSelectWallpaper.clickSafe {
            findNavController().navigate(R.id.action_settingFragment_to_wallpaperFragment)
        }

        binding.swEnableLightUp.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                SharePreferenceUtils.setEnableLightUpMode(isOn)
                showUi()
            }
        })

        binding.swEnableFlash.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                SharePreferenceUtils.setEnableFlashMode(isOn)
                showUi()
            }
        })

        binding.swEnableVibrate.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                SharePreferenceUtils.setEnableVibrate(isOn)
                showUi()
            }
        })

        binding.sbSensitivity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    SharePreferenceUtils.setSensitivity(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnHelpInformation.clickSafe {
            dialogInformationMoreHelp.show()
        }

        binding.btnSetNow.setOnTouchScale({
            dialogMoreHelp.show()
        }, 0.92f)

    }

    private val dialogInformationMoreHelp by lazy {
        DialogMoreHelp(
            requireContext(),
            R.string.app_name,
            R.string.content_information_more_help,
            R.string.ok_txt
        ) {

        }
    }

    private val dialogMoreHelp by lazy {
        DialogMoreHelp(
            requireContext(),
            R.string.title_more_help,
            R.string.content_more_help,
            R.string.go_to_setting
        ) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }
}