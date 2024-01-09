package com.fasipan.dont.touch.ui.intro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseActivity
import com.fasipan.dont.touch.databinding.ActivityIntroBinding
import com.fasipan.dont.touch.ui.main.MainActivity
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.openActivity
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.show

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding

    companion object {
        const val TAB_COUNT = 3
    }

    private var posView = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewIntro.show()
        binding.permissionFrame.gone()
        posView = 0

        initView()

        initListener()
    }

    private fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        binding.txtSkip.clickSafe {
            actionNext()
        }

        binding.txtNext.clickSafe {
            if (posView >= TAB_COUNT - 1) {
                actionNext()
            } else {
                binding.vpgTutorial.currentItem = posView + 1
            }
        }

        binding.swAudioPermission.clickSafe {
            requestAudioPermissions()
        }

        binding.txtContinue.setOnTouchScale({
            openActivity(MainActivity::class.java, true)
            SharePreferenceUtils.setShowFramePermission(false)
        }, 0.9f)
    }


    private fun actionNext() {
        if (SharePreferenceUtils.isShowFramePermission() && !isHasAudio()) {
            showPermissionFrame()
        } else {
            openActivity(MainActivity::class.java, true)
        }
    }

    private fun requestAudioPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this@IntroActivity,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", packageName, null)
            requestOpenSettingLauncher.launch(intent)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

            binding.swAudioPermission.setSwitchState(isGranted)
            binding.swAudioPermission.isEnabled = !isGranted
        }

    private val requestOpenSettingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.swAudioPermission.setSwitchState(true)
            binding.swAudioPermission.isEnabled = false
        } else {
            binding.swAudioPermission.setSwitchState(false)
            binding.swAudioPermission.isEnabled = true
        }
    }

    private fun isHasAudio(): Boolean {
        return hasPermission(Manifest.permission.RECORD_AUDIO)
    }


    private fun showPermissionFrame() {
        binding.viewIntro.gone()
        binding.permissionFrame.show()
    }


    private fun initView() {
        val adapter = TabIntroAdapter(
            this,
            supportFragmentManager,
            TAB_COUNT
        )
        binding.vpgTutorial.adapter = adapter

        binding.vpgTutorial.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                posView = position
                showTab(position)
                when (position) {
                    (TAB_COUNT - 1) -> {
                        binding.txtNext.text = getString(R.string.start)
                    }

                    else -> {
                        binding.txtNext.text = getString(R.string.next)
                    }
                }
            }

        })
    }

    private fun showTab(position: Int) {
        when (position) {
            1 -> {
                binding.tab1.setImageResource(R.drawable.selected_tab_indicator)
                binding.tab2.setImageResource(R.drawable.selected_tab_indicator)
                binding.tab3.setImageResource(R.drawable.default_tab_indicator)
            }

            2 -> {
                binding.tab1.setImageResource(R.drawable.selected_tab_indicator)
                binding.tab2.setImageResource(R.drawable.selected_tab_indicator)
                binding.tab3.setImageResource(R.drawable.selected_tab_indicator)
            }

            else -> {
                binding.tab1.setImageResource(R.drawable.selected_tab_indicator)
                binding.tab2.setImageResource(R.drawable.default_tab_indicator)
                binding.tab3.setImageResource(R.drawable.default_tab_indicator)

            }
        }
    }
}