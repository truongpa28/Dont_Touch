package com.fasipan.dont.touch.ui.intro

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.viewpager.widget.ViewPager
import com.fasipan.dont.touch.ui.main.MainActivity
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseActivity
import com.fasipan.dont.touch.databinding.ActivityIntroBinding
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.openActivity
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
    }

    private fun actionNext() {
        openActivity(MainActivity::class.java, true)
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
                        //.txtNext.setTextColor(ContextCompat.getColor(this@IntroActivity, R.color.main_color))
                    }

                    else -> {
                        binding.txtNext.text = getString(R.string.next)
                        //binding.txtNext.setTextColor(ContextCompat.getColor(this@IntroActivity, R.color.black_app))
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