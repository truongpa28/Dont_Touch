package com.fasipan.dont.touch.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.DialogMoreHelpBinding
import com.fasipan.dont.touch.utils.ex.setOnTouchScale

class DialogMoreHelp(
    private val context: Context,
    private val title: Int,
    private val content: Int,
    private val titleAction: Int,
    private val onAction: () -> Unit
) {
    private val binding by lazy {
        DialogMoreHelpBinding.inflate(LayoutInflater.from(context))
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

    fun show() {
        binding.btnAction.setOnTouchScale({
            onAction()
            hide()
        }, 0.9f)

        binding.txtContent.text = context.getString(content)
        binding.txtTitle.text = context.getString(title)
        binding.btnAction.text = context.getString(titleAction)

        if (!dialog.isShowing)
            dialog.show()
    }

}