package com.fasipan.dont.touch.service

import android.app.KeyguardManager
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.ActivityCustomLockScreenBinding
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomLockScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        super.onCreate(savedInstanceState)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE)
        keyguardLock.disableKeyguard()
        val mLayoutParams = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                4719872,
                PixelFormat.TRANSLUCENT
            )
        }
        window.attributes = mLayoutParams
        binding = ActivityCustomLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val timeClose = SharePreferenceUtils.getPlayDuration()

        when (timeClose) {
            0 -> {
                playDuration(5000)
            }

            1 -> {
                playDuration(15000)
            }

            2 -> {
                playDuration(30000)
            }

            3 -> {
                playDuration(60000)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishView()
            }
        })
    }

    private fun playDuration(time: Long) {
        Handler(Looper.myLooper()!!).postDelayed({
            finishView()
        }, time)
    }

    private fun finishView() {
        MediaPlayerUtils.stopMediaPlayer()
        finish()
    }

    override fun onWindowFocusChanged(z: Boolean) {
        super.onWindowFocusChanged(z)
        if (z) {
            window.decorView.systemUiVisibility = 5894
        }
    }

}