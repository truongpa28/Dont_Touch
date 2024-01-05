package com.fasipan.dont.touch.ui.more.unplugbattery

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.custom.OnSwitchStateChangeListener
import com.fasipan.dont.touch.databinding.FragmentUnplugBatteryBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.hide
import com.fasipan.dont.touch.utils.ex.show


class UnplugBatteryFragment : BaseFragment() {

    private lateinit var binding: FragmentUnplugBatteryBinding

    private val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryData(intent)
        }
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

    override fun onResume() {
        super.onResume()
        loadBatterySection()
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
        binding.swEnableUnplugPin.setSwitchState(SharePreferenceUtils.isEnableUnplugPin())
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }

        binding.swEnableUnplugPin.setSwitchStateChangeListener(object :
            OnSwitchStateChangeListener {
            override fun onSwitchStateChange(isOn: Boolean) {
                SharePreferenceUtils.setEnableUnplugPin(isOn)
            }
        })
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