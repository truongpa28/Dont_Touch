package com.fasipan.dont.touch.ui.splash

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.ActivitySplashBinding
import com.fasipan.dont.touch.ui.language.LanguageActivity
import com.fasipan.dont.touch.utils.ex.openActivity

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
            openActivity(LanguageActivity::class.java, true)
        }, 1500L)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

    }
}