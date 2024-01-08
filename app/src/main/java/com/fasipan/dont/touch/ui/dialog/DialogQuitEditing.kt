package com.fasipan.dont.touch.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.DialogQuitEditingBinding
import com.fasipan.dont.touch.utils.ex.setOnTouchScale

class DialogQuitEditing(private val context: Context) {
    private val binding by lazy {
        DialogQuitEditingBinding.inflate(LayoutInflater.from(context))
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

    fun show(actionSave: () -> Unit, actionQuit: () -> Unit) {
        binding.btnSave.setOnTouchScale({
            hide()
            actionSave()
        }, 0.95f)
        binding.btnQuit.setOnTouchScale({
            hide()
            actionQuit()
        }, 0.95f)

        if (!dialog.isShowing)
            dialog.show()
    }

}