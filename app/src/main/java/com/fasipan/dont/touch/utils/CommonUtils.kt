package com.fasipan.dont.touch.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.utils.ex.showToast

object CommonUtils {

    private const val PUBLISH_NAME = "7717501896293852484"

    private const val SUBJECT = "FeedBack"

    private const val email = "azizion0796@gmail.com"

    fun moreApp(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=$PUBLISH_NAME")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=$PUBLISH_NAME")
                )
            )
        }
    }

    fun rateApp(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${context.packageName}")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                )
            )
        }
    }

    fun shareApp(context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "https://play.google.com/store/apps/details?id=${context.packageName}"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share to"))
    }

    fun feedback(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse(
            "mailto:$email?subject=$SUBJECT&body="
        )
        intent.data = data
        try {
            context.startActivity(intent)
        } catch (ex: Exception) {
            context.showToast(context.getString(R.string.not_have_email_app_to_send_email))
            ex.printStackTrace()
        }
    }
}