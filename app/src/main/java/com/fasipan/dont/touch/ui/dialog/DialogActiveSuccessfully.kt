package com.fasipan.dont.touch.ui.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.databinding.DialogActiveSuccessfullyBinding
import com.fasipan.dont.touch.databinding.DialogNoInternetBinding
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.hide
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.show

class DialogActiveSuccessfully(private val context: Context) {
    private val binding by lazy {
        DialogActiveSuccessfullyBinding.inflate(LayoutInflater.from(context))
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
        binding.btnOk.setOnTouchScale( {
            hide()
        }, 0.95f)
        if (!dialog.isShowing)
            dialog.show()
    }

}