package com.fasipan.dont.touch.ui.home

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentHomeBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.ui.dialog.DialogDeleteAudio
import com.fasipan.dont.touch.ui.dialog.DialogOverlayPermission
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33
import com.fasipan.dont.touch.utils.ex.isSdkS
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val adapter by lazy {
        AudioAdapter()
    }

    private val dialogDeleteAudio by lazy {
        DialogDeleteAudio(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.txtTapToActive.isSelected = true
        binding.txtName.isSelected = true
        binding.rcyAudio.adapter = adapter
        LocalDataSource.getAllAudio().observe(viewLifecycleOwner) {
            adapter.setDataList(it)

            val itemChoose = it[SharePreferenceUtils.getPositionAudioChoose()]
            binding.txtName.text = if (itemChoose.isDefault) {
                try {
                    getString(itemChoose.nameInt)
                } catch (_: Exception) {
                    ""
                }
            } else {
                itemChoose.nameString
            }

            Glide.with(requireContext()).load(itemChoose.icon).into(binding.imgAvatar)
        }
    }

    private fun initListener() {
        binding.imgMenu.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_menuFragment)
        }
        binding.imgSetting.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
        binding.llClapToFind.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_clapToFindFragment)
        }
        binding.llUnplugCharged.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_unplugBatteryFragment)
        }
        binding.llFullCharged.clickSafe {
            findNavController().navigate(R.id.action_homeFragment_to_fullBatteryFragment)
        }

        adapter.setOnClickAudio { item, position ->
            if (position == 0) {
                findNavController().navigate(R.id.action_homeFragment_to_addAudioFragment)
            } else {
                item?.let {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_editAudioFragment,
                        bundleOf("pos" to position-1)
                    )
                }
            }
        }

        adapter.setOnClickDeleteAudio { item, position ->
            dialogDeleteAudio.show({
                item?.let { value ->
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        LocalDataSource.deleteAudio(
                            value
                        )
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().showToast("Add successfully")
                            dialogDeleteAudio.hide()
                        }
                    }
                }
            }, requireContext(), item?.nameString ?: "")
        }

        binding.txtTapToActive.setOnTouchScale({
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
        }, 0.9f)
    }

    private var isGotoSetting = false

    private var actionGotoSetting: (() -> Unit?)? = null

    private val dialogOverlayPermission by lazy {
        DialogOverlayPermission(requireContext())
    }

    private fun checkNotification() {
        if (isSdk33() && !requireContext().hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            //requireContext().showToast(getString(R.string.you_must_grant_permission_to_post_notifications))
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            actionService()
        }
    }

    private fun actionService() {
        if (SharePreferenceUtils.isAppServiceEnable()) {
            SharePreferenceUtils.setAppServiceEnable(false)
            endServiceApp()
        } else {
            startServiceApp()
            SharePreferenceUtils.setAppServiceEnable(true)
        }
        showChoose()
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

    private fun showChoose() {
        if (SharePreferenceUtils.isAppServiceEnable()) {
            binding.bgBottom.setImageResource(R.drawable.bg_choose_home_2)
            binding.txtTapToActive.setBackgroundResource(R.drawable.bg_btn_tap_to_deactive)
            binding.txtTapToActive.text = getText(R.string.tap_to_deactive)
        } else {
            binding.bgBottom.setImageResource(R.drawable.bg_choose_home_1)
            binding.txtTapToActive.setBackgroundResource(R.drawable.bg_btn_tap_to_active)
            binding.txtTapToActive.text = getText(R.string.tap_to_active)
        }
    }

    override fun onResume() {
        super.onResume()
        showChoose()
        if (SharePreferenceUtils.isEnableFullPin()) {
            binding.llFullCharged.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llFullCharged.background = null
        }
        if (SharePreferenceUtils.isEnableUnplugPin()) {
            binding.llUnplugCharged.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llUnplugCharged.background = null
        }
        if (SharePreferenceUtils.isEnableClapToFind()) {
            binding.llClapToFind.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llClapToFind.background = null
        }

        if (Settings.canDrawOverlays(requireContext()) && isGotoSetting) {
            dialogOverlayPermission.hide()
            isGotoSetting = false
            actionGotoSetting?.invoke()
        } else {
            isGotoSetting = false
        }
    }

    override fun onPause() {
        super.onPause()
        MediaPlayerUtils.stopMediaPlayer()
    }


    private var isClickBack = false
    override fun onBack() {
        if (isClickBack) {
            activity?.finish()
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.click_back),
                Toast.LENGTH_SHORT
            ).show()
            isClickBack = true
            Handler(Looper.getMainLooper()).postDelayed({
                isClickBack = false
            }, 1000L)
        }
    }
}