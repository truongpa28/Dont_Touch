package com.fasipan.dont.touch.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.ActivityCustomLockScreenBinding
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.initVibrator
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.showAndHide
import com.fasipan.dont.touch.utils.ex.startVibration
import com.fasipan.dont.touch.utils.ex.turnOffVibration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomLockScreenBinding

    private var funCount: CountDownTimer? = null

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        //region Create Screen

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        super.onCreate(savedInstanceState)
        /*val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE)
        keyguardLock.disableKeyguard()*/
        val mLayoutParams = if (Build.VERSION.SDK_INT >= 26) {
            val flag = if (SharePreferenceUtils.isEnableLightUpMode()) {
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            } else {
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            }
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                /*WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or *///WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                //or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                flag,
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
            1 -> {
                playDuration(15000)
            }

            2 -> {
                playDuration(30000)
            }

            3 -> {
                playDuration(60000)
            }

            else -> {
                playDuration(5000)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishView()
            }
        })

        binding.imgStop.setOnTouchScale({
            finishView()
        }, 0.9f)

        //endregion

        initVibrator(this)
        try {
            if (SharePreferenceUtils.isEnableFlashMode()) {
                LockServiceUtils.startFlash(this@LockActivity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (SharePreferenceUtils.isEnableVibrate()) {
                startVibration(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (SharePreferenceUtils.isEnableAudioSound()) {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
                val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = maxVolume * (SharePreferenceUtils.getVolumeAudioSound()) / 100
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    volume,
                    0
                )
                MediaPlayerUtils.playAudioWarning(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        countTime()

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, IntentFilter("action_finish"), RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, IntentFilter("action_finish"))
        }


    }

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                "action_finish" -> {
                    finishView()
                }
            }
        }
    }

    private fun countTime() {
        funCount = object : CountDownTimer(100000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.viewChop.showAndHide()
                }
            }

            override fun onFinish() {}
        }
        funCount?.start()
    }

    private fun playDuration(time: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(time)
            withContext(Dispatchers.Main) {
                finishView()
            }
        }
    }

    private fun finishView() {
        MediaPlayerUtils.stopMediaPlayer()
        LockServiceUtils.stopFlash()
        turnOffVibration()
        funCount?.cancel()
        finish()
    }

    override fun onWindowFocusChanged(z: Boolean) {
        super.onWindowFocusChanged(z)
        if (z) {
            window.decorView.systemUiVisibility = 5894
        }
    }

    override fun onResume() {
        super.onResume()
        if (!MediaPlayerUtils.isPlaying() && SharePreferenceUtils.isEnableAudioSound()) {
            MediaPlayerUtils.playAudioWarning(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerUtils.stopMediaPlayer()
        LockServiceUtils.stopFlash()
        turnOffVibration()
        funCount?.cancel()
        unregisterReceiver(receiver)
    }


}