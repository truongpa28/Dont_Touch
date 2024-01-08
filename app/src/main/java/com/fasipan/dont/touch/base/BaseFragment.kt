package com.fasipan.dont.touch.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.service.ChargingService

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
        requireContext().stopService(
            Intent(
                requireContext().applicationContext,
                ChargingService::class.java
            )
        )
    }

}