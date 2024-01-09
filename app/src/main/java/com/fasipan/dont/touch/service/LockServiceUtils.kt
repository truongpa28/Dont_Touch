package com.fasipan.dont.touch.service

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.SpeedFlashUtils

object LockServiceUtils {

    private var cameraManager: CameraManager? = null
    private var timeCountDown: CountDownTimer? = null


    val handlerFlash = Handler()
    private var runnableFlash: Runnable? = null
    fun startFlash(context: Context, timeLoop: Long = 100000, speed: Long? = null) {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val typeFlash = SharePreferenceUtils.getTypeFlash()
        var isFlashOn = false
        val spx: Long = speed ?: if (SharePreferenceUtils.isEnableSpeedMode()) {
            SpeedFlashUtils.getFlashDelayByType(SharePreferenceUtils.getTypeFlash())
        } else {
            SpeedFlashUtils.SPEED_FLASH_DEFAULT.toLong()
        }

        runnableFlash = object : Runnable {
            override fun run() {
                try {
                    isFlashOn = !isFlashOn
                    turnFlashWithStyle(context, typeFlash, isFlashOn)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handlerFlash.postDelayed(this, spx)
            }
        }
        handlerFlash.post(runnableFlash as Runnable)

        /*timeCountDown = object : CountDownTimer(timeLoop, spx) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    isFlashOn = !isFlashOn
                    turnFlashWithStyle(context, typeFlash, isFlashOn)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                stopFlash()
            }
        }
        timeCountDown?.start()*/
    }

    private fun turnFlashWithStyle(context: Context, typeFlash: Int, isFlashOn: Boolean) {
        var isFlashOn1 = isFlashOn
        if (cameraManager == null) {
            cameraManager =
                context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        }
        if (typeFlash == 0) {
            isFlashOn1 = !isFlashOn1
            if (isFlashOn1) {
                val cameraId = cameraManager?.cameraIdList?.get(0)
                cameraManager?.setTorchMode(cameraId!!, true)
            } else {
                val cameraId = cameraManager?.cameraIdList?.get(0)
                cameraManager?.setTorchMode(cameraId!!, false)
            }
        } else {
            val cameraId = cameraManager?.cameraIdList?.get(0)
            cameraManager?.setTorchMode(cameraId!!, true)
            Handler(Looper.getMainLooper()).postDelayed({
                cameraManager?.setTorchMode(cameraId!!, false)
            }, 40)
            Handler(Looper.getMainLooper()).postDelayed({
                cameraManager?.setTorchMode(cameraId!!, true)
            }, 80)
            Handler(Looper.getMainLooper()).postDelayed({
                cameraManager?.setTorchMode(cameraId!!, false)
            }, 120)
        }
    }

    fun stopFlash() {
        /*timeCountDown?.cancel()
        timeCountDown = null*/
        try {
            runnableFlash?.let { it1 -> handlerFlash.removeCallbacks(it1) }
            val cameraId = cameraManager?.cameraIdList?.get(0)
            if (cameraId != null) {
                cameraManager?.setTorchMode(cameraId, false)
                cameraManager = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}