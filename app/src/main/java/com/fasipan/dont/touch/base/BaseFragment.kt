package com.fasipan.dont.touch.base

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.service.ChargingService
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33
import com.fasipan.dont.touch.utils.ex.resetAvailableClick

abstract class BaseFragment() : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    open fun onBack() {
        findNavController().popBackStack()
        resetAvailableClick()
    }

    fun isHasNotification() : Boolean {
        return !(isSdk33() && !requireContext().hasPermission(Manifest.permission.POST_NOTIFICATIONS))
    }

    fun startServiceApp() {
        requireContext().startService(
            Intent(
                requireContext().applicationContext,
                ChargingService::class.java
            )
        )
    }

    fun endServiceApp() {
        if (!SharePreferenceUtils.isAppServiceEnable()
            && !SharePreferenceUtils.isEnableClapToFind()
            && !SharePreferenceUtils.isEnableUnplugPin()
            && !SharePreferenceUtils.isEnableFullPin()
        ) {
            requireContext().stopService(
                Intent(
                    requireContext().applicationContext,
                    ChargingService::class.java
                )
            )
        }
    }

}