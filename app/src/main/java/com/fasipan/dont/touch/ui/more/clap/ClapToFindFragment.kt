package com.fasipan.dont.touch.ui.more.clap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentClapToFindBinding
import com.fasipan.dont.touch.ui.dialog.DialogClapToFind
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.setOnTouchScale

class ClapToFindFragment : BaseFragment() {

    private lateinit var binding: FragmentClapToFindBinding

    private val dialogClapToFind by lazy {
        DialogClapToFind(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClapToFindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        showViewUi()
    }

    private fun showViewUi() {
        if (SharePreferenceUtils.isEnableClapToFind()) {
            binding.imgStatus.setImageResource(R.drawable.frame_stop)
            binding.txtStatus.text = getString(R.string.stop)
            binding.txtStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hong_app_1
                )
            )
        } else {
            binding.imgStatus.setImageResource(R.drawable.frame_active)
            binding.txtStatus.text = getString(R.string.activate)
            binding.txtStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.xanh_app_1
                )
            )
        }
    }

    private fun initListener() {
        binding.imgBack.clickSafe { onBack() }

        binding.llStatus.setOnTouchScale({
            if (SharePreferenceUtils.isEnableClapToFind()) {
                SharePreferenceUtils.setEnableClapToFind(false)
                showViewUi()
            } else {
                dialogClapToFind.show {
                    SharePreferenceUtils.setEnableClapToFind(true)
                    showViewUi()
                }
            }
        }, 0.9f)
    }
}