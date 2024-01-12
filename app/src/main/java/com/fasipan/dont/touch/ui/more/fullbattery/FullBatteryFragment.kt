package com.fasipan.dont.touch.ui.more.fullbattery

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.custom.OnSwitchStateChangeListener
import com.fasipan.dont.touch.databinding.FragmentFullBatteryBinding
import com.fasipan.dont.touch.ui.dialog.DialogOverlayPermission
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33
import com.fasipan.dont.touch.utils.ex.isSdkS
import com.fasipan.dont.touch.utils.ex.showToast

class FullBatteryFragment : BaseFragment() {

    private lateinit var binding: FragmentFullBatteryBinding

    private val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryData(intent)
        }
    }

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
        binding.swEnableFullPin.setSwitchState(SharePreferenceUtils.isEnableFullPin())
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }

        binding.swEnableFullPin.setSwitchStateChangeListener(object : OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                if (isOn) {
                    if (!Settings.canDrawOverlays(requireContext())) {
                        dialogOverlayPermission.show(  actionGotoSetting =  {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${requireContext().packageName}"),
                            )
                            startActivity(intent)
                            isGotoSetting = true
                        }, actionDismiss = {
                            binding.swEnableFullPin.setSwitchState(SharePreferenceUtils.isEnableFullPin())
                        })
                        actionGotoSetting = { checkNotification() }
                    } else {
                        checkNotification()
                    }
                } else {
                    SharePreferenceUtils.setEnableFullPin(false)
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
            //requireContext().showToast(getString(R.string.you_must_grant_permission_to_post_notifications))
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startServiceApp()
            SharePreferenceUtils.setEnableFullPin(true)
        }
    }

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (isSdkS()) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    startServiceApp()
                    SharePreferenceUtils.setEnableFullPin(true)
                } else {
                    SharePreferenceUtils.setEnableFullPin(false)
                    endServiceApp()
                    requireContext().showToast(getString(R.string.permission_denied))
                }
            }
        } else {
            SharePreferenceUtils.setEnableFullPin(false)
            endServiceApp()
            requireContext().showToast(getString(R.string.permission_denied))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBatteryData(intent: Intent) {
        val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
        binding.txtBattery.text = "--%"
        binding.txtTemperature.text = "--°C"
        binding.txtVoltage.text = "-- V"
        binding.txtHealth.text = "--"
        binding.txtTechnology.text = "--"
        binding.txtCapacity.text = "-- mAh"

        if (present) {
            showBattery(intent)
            showStatus(intent)
            showTemperature(intent)
            showVoltage(intent)
            showHealth(intent)
            showTechnology(intent)
            showCapacity()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showBattery(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level != -1 && scale != -1) {
            val batteryPct = ((level / scale.toFloat()) * 100).toInt()
            binding.txtBattery.text = "$batteryPct%"
        }
    }

    private fun showStatus(intent: Intent) {
        when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING-> {
                binding.txtStatus.text = getString(R.string.charging)
            }

            BatteryManager.BATTERY_STATUS_FULL -> {
                binding.txtStatus.text = getString(R.string.full)
            }

            else -> {
                binding.txtStatus.text = getString(R.string.discharging)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showTemperature(intent: Intent) {
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        if (temperature > 0) {
            val temp = temperature / 10f;
            binding.txtTemperature.text = "$temp°C"
        }
    }

    private fun showVoltage(intent: Intent) {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        if (voltage > 0) {
            binding.txtVoltage.text = String.format("%.1f V", voltage / 1000f)
        }
    }

    private fun showHealth(intent: Intent) {
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
        var healthLbl = ""
        when (health) {
            BatteryManager.BATTERY_HEALTH_COLD -> {
                healthLbl = getString(R.string.cold)
            }

            BatteryManager.BATTERY_HEALTH_DEAD -> {
                healthLbl = getString(R.string.dead)
            }

            BatteryManager.BATTERY_HEALTH_GOOD -> {
                healthLbl = getString(R.string.good)
            }

            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                healthLbl = getString(R.string.over_voltage)
            }

            BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                healthLbl = getString(R.string.over_heat)
            }

            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> {
                healthLbl = getString(R.string.unspecified)
            }

            BatteryManager.BATTERY_HEALTH_UNKNOWN -> {
                binding.txtHealth.text = healthLbl
            }

            else -> {}
        }
        binding.txtHealth.text = healthLbl
    }

    private fun showTechnology(intent: Intent) {
        if (intent.extras != null) {
            val technology = intent.extras?.getString(BatteryManager.EXTRA_TECHNOLOGY);

            if ("" != technology) {
                binding.txtTechnology.text = "$technology"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showCapacity() {
        val capacity = getBatteryCapacity(requireContext())
        if (capacity > 0) {
            binding.txtCapacity.text = "~$capacity mAh"
        }
    }

    private fun getBatteryCapacity(ctx: Context): Long {
        val mBatteryManager = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val chargeCounter =
            mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return (chargeCounter.toFloat() / (capacity.toFloat() * 10)).toLong()
    }
}