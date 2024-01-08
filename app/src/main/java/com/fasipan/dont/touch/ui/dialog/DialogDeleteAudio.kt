package com.fasipan.dont.touch.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.DialogDeleteAudioBinding
import com.fasipan.dont.touch.databinding.DialogQuitEditingBinding
import com.fasipan.dont.touch.utils.ex.setOnTouchScale

class DialogDeleteAudio(private val context: Context) {
    private val binding by lazy {
        DialogDeleteAudioBinding.inflate(LayoutInflater.from(context))
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

    @SuppressLint("SetTextI18n")
    fun show(actionSave: () -> Unit, context: Context, string : String) {
        binding.txtContent.text = "${context.getString(R.string.txt_content_delete_audio)} $string?"
        binding.btnYes.setOnTouchScale({
            actionSave()
            hide()
        }, 0.95f)
        binding.btnNo.setOnTouchScale({
            hide()
        }, 0.95f)

        if (!dialog.isShowing)
            dialog.show()
    }

}