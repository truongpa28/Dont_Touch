package com.fasipan.dont.touch.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.DialogOverlayPermissionBinding
import com.fasipan.dont.touch.utils.ex.clickSafe

class DialogOverlayPermission(private val context: Context) {
    private val binding by lazy {
        DialogOverlayPermissionBinding.inflate(LayoutInflater.from(context))
    }

    private val dialog: AlertDialog by lazy {
        AlertDialog.Builder(context, R.style.dialog_transparent_width).setView(binding.root)
            .create()
    }

    init {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun hide() {
        dialog.dismiss()
    }

    fun show(actionGotoSetting: () -> Unit) {
        binding.btnGotoSetting.clickSafe {
            actionGotoSetting()
        }
        if (!dialog.isShowing)
            dialog.show()
    }


}