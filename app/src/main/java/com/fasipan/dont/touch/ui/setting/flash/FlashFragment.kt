package com.fasipan.dont.touch.ui.setting.flash

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentFlashBinding
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.SpeedFlashUtils
import com.fasipan.dont.touch.utils.ex.clickSafe

class FlashFragment : BaseFragment() {

    private lateinit var binding: FragmentFlashBinding

    private var isPlayFlash = false

    private var cameraManager: CameraManager? = null
    private var timeCountDown: CountDownTimer? = null

    private val adapter by lazy {
        FlashAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        adapter.setDataList(SpeedFlashUtils.listSpeedFlash)
        binding.rcyFlash.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }
        adapter.setOnClickItem { _, position ->
            stopFlash()
            SharePreferenceUtils.setTypeFlash(position)
            adapter.notifyDataSetChanged()
            startFlash()
        }
    }

    override fun onPause() {
        super.onPause()
        stopFlash()
    }

    override fun onDestroy() {
        stopFlash()
        super.onDestroy()
    }

    private fun startFlash() {
        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        isPlayFlash = true
        var isFlashOn = false
        val speed = SpeedFlashUtils.getFlashDelayByType(SharePreferenceUtils.getTypeFlash())

        timeCountDown = object : CountDownTimer(4000L, speed) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    isFlashOn = !isFlashOn
                    testFlash(isFlashOn)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                stopFlash()
                isPlayFlash = false
            }
        }
        timeCountDown?.start()
    }
    private fun testFlash(isFlashOn: Boolean) {
        var isFlashOn1 = isFlashOn
        isFlashOn1 = !isFlashOn1
        if (isFlashOn1) {
            val cameraId = cameraManager?.cameraIdList?.get(0)
            cameraManager?.setTorchMode(cameraId!!, true)
        } else {
            val cameraId = cameraManager?.cameraIdList?.get(0)
            cameraManager?.setTorchMode(cameraId!!, false)
        }
    }

    private fun stopFlash() {
        timeCountDown?.cancel()
        val cameraId = cameraManager?.cameraIdList?.get(0)
        if (cameraId != null) {
            cameraManager?.setTorchMode(cameraId, false)
            cameraManager = null
        }
        isPlayFlash = false
    }

}