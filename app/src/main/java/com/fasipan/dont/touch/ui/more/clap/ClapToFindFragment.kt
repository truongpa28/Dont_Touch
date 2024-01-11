package com.fasipan.dont.touch.ui.more.clap

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentClapToFindBinding
import com.fasipan.dont.touch.ui.dialog.DialogClapToFind
import com.fasipan.dont.touch.ui.dialog.DialogOverlayPermission
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33
import com.fasipan.dont.touch.utils.ex.isSdkS
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.showToast

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

    @SuppressLint("SetTextI18n")
    private fun showViewUi() {
        if (SharePreferenceUtils.isEnableClapToFind()) {
            binding.imgStatus.setImageResource(R.drawable.frame_stop)
            binding.txtStatus.text = "Stop"
            binding.txtStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.hong_app_1
                )
            )
        } else {
            binding.imgStatus.setImageResource(R.drawable.frame_active)
            binding.txtStatus.text = "Activate"
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
                endServiceApp()
                showViewUi()
            } else {
                if (!Settings.canDrawOverlays(requireContext())) {
                    dialogOverlayPermission.show {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${requireContext().packageName}"),
                        )
                        startActivity(intent)
                        isGotoSetting = true
                    }
                    actionGotoSetting = { checkNotification() }
                } else {
                    checkNotification()
                }
            }
        }, 0.9f)
    }

    private var isGotoSetting = false

    private var actionGotoSetting: (() -> Unit?)? = null

    private val dialogOverlayPermission by lazy {
        DialogOverlayPermission(requireContext())
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(requireContext()) && isGotoSetting) {
            dialogOverlayPermission.hide()
            isGotoSetting = false
            actionGotoSetting?.invoke()
        } else {
            isGotoSetting = false
        }
    }

    private fun checkNotification() {
        if (isSdk33() && !requireContext().hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            //requireContext().showToast(getString(R.string.you_must_grant_permission_to_post_notifications))
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            actionService()
        }
    }

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (isSdkS()) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    actionService()
                } else {
                    actionService()
                    requireContext().showToast(getString(R.string.permission_denied))
                }
            }
        } else {
            actionService()
            requireContext().showToast(getString(R.string.permission_denied))
        }
    }

    private fun actionService() {
        dialogClapToFind.show {
            startServiceApp()
            SharePreferenceUtils.setEnableClapToFind(true)
            showViewUi()
        }
    }
}