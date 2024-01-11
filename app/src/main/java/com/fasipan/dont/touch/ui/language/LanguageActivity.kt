package com.fasipan.dont.touch.ui.language

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.fasipan.dont.touch.base.BaseActivity
import com.fasipan.dont.touch.databinding.ActivityLanguageBinding
import com.fasipan.dont.touch.model.LanguageModel
import com.fasipan.dont.touch.ui.intro.IntroActivity
import com.fasipan.dont.touch.utils.LanguageUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.openActivity
import com.fasipan.dont.touch.utils.ex.setLanguageApp
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.showOrGone
import com.fasipan.dont.touch.utils.ex.showOrHide

class LanguageActivity : BaseActivity(), ClickLanguageListener {

    private lateinit var binding: ActivityLanguageBinding

    private val adapter by lazy {
        LanguageAdapter(this, LanguageUtils.listLanguage, this)
    }

    private var isSetting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isSetting = intent.getBooleanExtra("setting", false)

        initView()

        initListener()

    }

    private fun initListener() {
        binding.txtDone.setOnTouchScale({
            val code = LanguageUtils.listLanguage[adapter.getChoose()].key
            setLanguageApp(code)
            SharePreferenceUtils.setCodeLanguageChoose(code)
            if (isSetting) {
                onBack()
            } else {
                openActivity(IntroActivity::class.java, true)
            }
        }, 0.9f)

        binding.imgBack.setOnTouchScale({
            onBack()
        }, 0.9f)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSetting) {
                    onBack()
                }
            }
        })
    }

    private fun onBack() {
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        adapter.setChoose(LanguageUtils.getPositionChoose(this))
        binding.rcyLanguage.adapter = adapter
        binding.imgBack.showOrHide(isSetting)

        if (SharePreferenceUtils.isFirstChooseLanguage()) {
            binding.txtTitle.text = "Select Language"
            binding.txtDone.text = "Done"
        }
    }

    override fun clickLanguage(position: Int, languageModel: LanguageModel) {
        adapter.setChoose(position)
    }
}