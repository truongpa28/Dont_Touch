package com.fasipan.dont.touch.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseActivity
import com.fasipan.dont.touch.databinding.ActivityMainBinding
import com.fasipan.dont.touch.service.ChargingService
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(binding.containerFragment.id) as NavHostFragment
        navController = navHostFragment!!.navController

        if (SharePreferenceUtils.isFirstRequestNotification()) {
            try {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        112
                    )
                    SharePreferenceUtils.setFirstRequestNotification(false)
                }
            } catch (_: Exception) { }
        }

        if ((SharePreferenceUtils.isAppServiceEnable()
            || SharePreferenceUtils.isEnableClapToFind()
            || SharePreferenceUtils.isEnableUnplugPin()
            || SharePreferenceUtils.isEnableFullPin()
            )  && isHasNotification()) {
            startService(
                Intent(
                    applicationContext,
                    ChargingService::class.java
                )
            )
        }
    }

    private fun isHasNotification() : Boolean {
        return !(isSdk33() && !hasPermission(Manifest.permission.POST_NOTIFICATIONS))
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}