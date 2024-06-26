package com.fasipan.dont.touch.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentHomeBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.ui.dialog.DialogActiveSuccessfully
import com.fasipan.dont.touch.ui.dialog.DialogAudioPermission
import com.fasipan.dont.touch.ui.dialog.DialogDeleteAudio
import com.fasipan.dont.touch.ui.dialog.DialogNotificationPermission
import com.fasipan.dont.touch.ui.dialog.DialogOverlayPermission
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.hasPermission
import com.fasipan.dont.touch.utils.ex.isSdk33
import com.fasipan.dont.touch.utils.ex.scrollToTop
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

    private val dialogNotificationPermission by lazy {
        DialogNotificationPermission(requireContext())
    }

    private val dialogActiveSuccessfully by lazy {
        DialogActiveSuccessfully(requireContext())
    }

    private val dialogAudioPermission by lazy {
        DialogAudioPermission(requireContext())
    }

    private var scrollY = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isFirstOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.txtTapToActive.isSelected = true
        binding.txtName.isSelected = true
        binding.txtTitle.isSelected = true
        binding.rcyAudio.adapter = adapter
        LocalDataSource.getAllAudio().observe(viewLifecycleOwner) {
            adapter.setDataListWithAction(it) {
                Handler(Looper.myLooper()!!).postDelayed({
                    binding.nestView.post {
                        binding.nestView.scrollToTop(scrollY)
                    }
                }, 200L)
            }

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

            SharePreferenceUtils.setAudioWaring(itemChoose.sound)

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
        binding.llClapToFind.setOnTouchScale({
            findNavController().navigate(R.id.action_homeFragment_to_clapToFindFragment)
        }, 0.9f)
        binding.llUnplugCharged.setOnTouchScale({
            findNavController().navigate(R.id.action_homeFragment_to_unplugBatteryFragment)
        }, 0.9f)
        binding.llFullCharged.setOnTouchScale({
            findNavController().navigate(R.id.action_homeFragment_to_fullBatteryFragment)
        }, 0.9f)

        adapter.setOnClickAudio { item, position ->
            if (position == 0) {
                if (isHasAudio()) {
                    findNavController().navigate(R.id.action_homeFragment_to_addAudioFragment)
                } else {
                    requestAudioPermissions()
                }
            } else {
                item?.let {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_editAudioFragment,
                        bundleOf("pos" to position - 1)
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
                            requireContext().showToast(getString(R.string.delete_successfully))
                            if (position == SharePreferenceUtils.getPositionAudioChoose()) {
                                SharePreferenceUtils.setPositionAudioChoose(1)
                            }
                            dialogDeleteAudio.hide()
                        }
                    }
                }
            }, requireContext(), item?.nameString ?: "")
        }

        binding.txtTapToActive.setOnTouchScale({
            if (!Settings.canDrawOverlays(requireContext())) {
                dialogOverlayPermission.show(actionGotoSetting = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${requireContext().packageName}"),
                    )
                    startActivity(intent)
                    isGotoSetting = true
                }, actionDismiss = {})
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
            dialogNotificationPermission.show {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                requestNotificationLauncher.launch(intent)
            }

            dialogNotificationPermission.onCancel {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) requireContext().showToast(getString(R.string.permission_denied))
            }
        } else {
            actionService()
        }
    }

    private val requestNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            actionService()
        } else {
            requireContext().showToast(getString(R.string.permission_denied))
        }
    }

    private fun actionService() {
        if (SharePreferenceUtils.isAppServiceEnable()) {
            SharePreferenceUtils.setAppServiceEnable(false)
            endServiceApp()
        } else {
            startServiceApp()
            SharePreferenceUtils.setAppServiceEnable(true)
            dialogActiveSuccessfully.show()
        }
        showChoose()
    }

    private fun showChoose() {
        if (SharePreferenceUtils.isAppServiceEnable() && isHasNotification()) {
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
        if (SharePreferenceUtils.isEnableFullPin() && isHasNotification()) {
            binding.llFullCharged.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llFullCharged.background = null
        }
        if (SharePreferenceUtils.isEnableUnplugPin() && isHasNotification()) {
            binding.llUnplugCharged.setBackgroundResource(R.drawable.bg_item_more_home)
        } else {
            binding.llUnplugCharged.background = null
        }
        if (SharePreferenceUtils.isEnableClapToFind() && isHasNotification()) {
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
        scrollY = binding.nestView.scrollY
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

    private fun requestAudioPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            dialogAudioPermission.show {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                requestOpenSettingLauncher.launch(intent)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                findNavController().navigate(R.id.action_homeFragment_to_addAudioFragment)
            } else {
                requireContext().showToast(getString(R.string.permission_denied))
            }
        }

    private val requestOpenSettingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_addAudioFragment)
        } else {
            requireContext().showToast(getString(R.string.permission_denied))
        }
    }

    private fun isHasAudio(): Boolean {
        return requireContext().hasPermission(Manifest.permission.RECORD_AUDIO)
    }
}