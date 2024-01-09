package com.fasipan.dont.touch.ui.splash

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fasipan.dont.touch.databinding.ActivitySplashBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.ui.main.MainActivity
import com.fasipan.dont.touch.utils.data.DataAudioUtils
import com.fasipan.dont.touch.utils.ex.openActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen", "SourceLockedOrientationActivity")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            openActivity(MainActivity::class.java, true)
        }, 1500L)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        lifecycleScope.launch(Dispatchers.IO) {
            LocalDataSource.addAllAudio(DataAudioUtils.listDefault)
        }

    }
}