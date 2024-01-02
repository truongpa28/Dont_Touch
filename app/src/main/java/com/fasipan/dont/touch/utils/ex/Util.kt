package com.fasipan.dont.touch.utils.ex

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import java.util.concurrent.TimeUnit


fun Long.toTimeFormat(): String {

    // long minutes = (milliseconds / 1000) / 60;
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)

    // long seconds = (milliseconds / 1000);
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this - minutes * 60 * 1000)
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

fun getFlagUrl(code: String): String {
    val base = "https://static.mytuner.mobi/media/countries/"
    return if (code.equals("UK", true)) {
        base + "gb.png"
    } else {
        "$base${code.lowercase()}.png"
    }
}

fun Int.toDp(context: Context): Int {
    return context.resources.displayMetrics.density.toInt() * this
}

fun String.getUrlThemCall() = "file:///android_asset/background/$this"

fun setLanguageApp(code: String) {
    val localeList = LocaleListCompat.forLanguageTags(code)
    AppCompatDelegate.setApplicationLocales(localeList)
}