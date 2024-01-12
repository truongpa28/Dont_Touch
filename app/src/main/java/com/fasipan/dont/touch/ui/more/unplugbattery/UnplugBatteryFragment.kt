package com.fasipan.dont.touch.ui.more.unplugbattery

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.custom.OnSwitchStateChangeListener
import com.fasipan.dont.touch.databinding.FragmentUnplugBatteryBinding
import com.fasipan.dont.touch.ui.dialog.DialogNotificationPermission
import com.fasipan.dont.touch.ui.dialog.DialogOverlayPermission
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33
import com.fasipan.dont.touch.utils.ex.isSdkS
import com.fasipan.dont.touch.utils.ex.show
import com.fasipan.dont.touch.utils.ex.showToast


class UnplugBatteryFragment : BaseFragment() {

    private lateinit var binding: FragmentUnplugBatteryBinding

    private val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryData(intent)
        }
    }

    private val dialogNotificationPermission by lazy {
        DialogNotificationPermission(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUnplugBatteryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(batteryInfoReceiver)
    }

    private fun loadBatterySection() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().registerReceiver(batteryInfoReceiver, intentFilter)
    }


    private fun initView() {
        binding.txtTitle.isSelected = true
        binding.swEnableUnplugPin.setSwitchState(SharePreferenceUtils.isEnableUnplugPin() && isHasNotification())
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }

        binding.swEnableUnplugPin.setSwitchStateChangeListener(object :
            OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                if (isOn) {
                    if (!Settings.canDrawOverlays(requireContext())) {
                        dialogOverlayPermission.show(actionGotoSetting = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${requireContext().packageName}"),
                            )
                            startActivity(intent)
                            isGotoSetting = true
                        }, actionDismiss = {
                            initView()
                        })
                        actionGotoSetting = { checkNotification() }
                    } else {
                        checkNotification()
                    }
                } else {
                    SharePreferenceUtils.setEnableUnplugPin(false)
                    initView()
                }
            }
        })
    }

    private var isGotoSetting = false

    private var actionGotoSetting: (() -> Unit?)? = null

    private val dialogOverlayPermission by lazy {
        DialogOverlayPermission(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadBatterySection()
        if (Settings.canDrawOverlays(requireContext()) && isGotoSetting) {
            dialogOverlayPermission.hide()
            isGotoSetting = false
            actionGotoSetting?.invoke()
        } else {
            isGotoSetting = false
        }
    }

    private fun checkNotification() {
        if (isSdk33() && !requireContext().hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            dialogNotificationPermission.show {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                requestNotificationLauncher.launch(intent)
            }

            dialogNotificationPermission.onCancel {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    SharePreferenceUtils.setEnableUnplugPin(false)
                    endServiceApp()
                    requireContext().showToast(getString(R.string.permission_denied))
                    initView()
                }
            }
        } else {
            startServiceApp()
            SharePreferenceUtils.setEnableUnplugPin(true)
            initView()
        }
    }

    private val requestNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startServiceApp()
            SharePreferenceUtils.setEnableUnplugPin(true)
            initView()
        } else {
            SharePreferenceUtils.setEnableUnplugPin(false)
            endServiceApp()
            requireContext().showToast(getString(R.string.permission_denied))
            initView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBatteryData(intent: Intent) {
        val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)

        binding.llWaring.gone()

        if (present) {
            when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                BatteryManager.BATTERY_PLUGGED_WIRELESS,
                BatteryManager.BATTERY_PLUGGED_USB,
                BatteryManager.BATTERY_PLUGGED_AC -> {
                    binding.llWaring.gone()
                }

                else -> {
                    binding.llWaring.show()
                }
            }
        }
    }
}