package com.fasipan.dont.touch.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentHomeBinding
import com.fasipan.dont.touch.databinding.FragmentMenuBinding
import com.fasipan.dont.touch.ui.language.LanguageActivity
import com.fasipan.dont.touch.utils.CommonUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.openActivity
import com.fasipan.dont.touch.utils.ex.showToast

class MenuFragment : BaseFragment() {

    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.txtTitle.isSelected = true
    }

    private fun initListener() {
        binding.imgBack.clickSafe {
            onBack()
        }
        binding.llLanguage.clickSafe {
            requireContext().openActivity(LanguageActivity::class.java, bundleOf("setting" to true))
        }

        binding.llRateApp.clickSafe {
            CommonUtils.rateApp(requireContext())
        }

        binding.llShareApp.clickSafe {
            CommonUtils.shareApp(requireContext())
        }
        binding.llSendFeedback.clickSafe {
            CommonUtils.feedback(requireContext())
        }

        binding.llMoreApp.clickSafe {
            CommonUtils.moreApp(requireContext())
        }

        binding.llPrivacyPolicy.clickSafe {
            requireContext().showToast("Develop")
        }
    }
}