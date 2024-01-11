package com.fasipan.dont.touch.service

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BATTERY_CHANGED
import android.content.Intent.ACTION_POWER_CONNECTED
import android.content.Intent.ACTION_POWER_DISCONNECTED
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.Intent.ACTION_SCREEN_ON
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.getBatteryLevel


class ChargingListener : BroadcastReceiver() {

    @SuppressLint("InvalidWakeLockTag")
    private fun lock(context: Context, boqua: Boolean = false) {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.e("isKeyguardLocked", "${keyguardManager.isKeyguardLocked}")

        if (!boqua) {
            if (!keyguardManager.isKeyguardLocked) {
                return
            }
        }

        val intent = Intent(context, LockActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        Log.e("truongpa", "Call_startActivity")
        context.startActivity(intent)
    }

    fun turnOnScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?

        if (powerManager != null) {
            // Kiểm tra xem màn hình đã tắt chưa
            if (!powerManager.isScreenOn) {
                val wakeLock = powerManager.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp:ScreenLock"
                )

                wakeLock.acquire(60 * 1000L)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { SharePreferenceUtils.init(it) }
        when (intent?.action) {

            ACTION_SCREEN_OFF -> {
                SharePreferenceUtils.setScreenOn(false)
            }

            ACTION_SCREEN_ON -> {
                SharePreferenceUtils.setScreenOn(true)
            }

            ACTION_POWER_CONNECTED -> {
                try {
                    val battery = context?.getBatteryLevel() ?: 0
                    SharePreferenceUtils.setBatteryChanged(battery)
                    if (battery == 100) {
                        SharePreferenceUtils.setBatteryChanged(battery)
                        if (SharePreferenceUtils.isEnableFullPin()) {
                            context?.let { lock(it, true) }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            ACTION_POWER_DISCONNECTED -> {
                if (SharePreferenceUtils.isEnableUnplugPin()) {
                    context?.let { lock(it, true) }
                } else {
                    if (SharePreferenceUtils.isEnableFullPin() && (SharePreferenceUtils.getBatteryChanged() == 100))
                        context?.sendBroadcast(Intent("action_finish"))
                }
            }

            ACTION_BATTERY_CHANGED -> {
                val battery = context?.getBatteryLevel() ?: 0
                if (battery == 100 && battery != SharePreferenceUtils.getBatteryChanged()) {
                    SharePreferenceUtils.setBatteryChanged(battery)
                    if (SharePreferenceUtils.isEnableFullPin()) {
                        context?.let { lock(it, true) }
                    }
                } else {
                    SharePreferenceUtils.setBatteryChanged(battery)
                }
            }

            "action_touch" -> {
                context?.let { lock(it) }
            }

            "action_clap" -> {
                context?.let { lock(it) }
            }
        }

    }
}