package com.fasipan.dont.touch.utils

import android.content.Context
import android.content.SharedPreferences

object SharePreferenceUtils {

    private const val PER_NAME = "data_app_ghost_radar"

    private lateinit var sharePref: SharedPreferences

    fun init(context: Context) {
        if (!SharePreferenceUtils::sharePref.isInitialized) {
            sharePref = context.getSharedPreferences(PER_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun <T> saveKey(key: String, value: T) {
        when (value) {
            is String -> sharePref.edit().putString(key, value).apply()
            is Int -> sharePref.edit().putInt(key, value).apply()
            is Boolean -> sharePref.edit().putBoolean(key, value).apply()
            is Long -> sharePref.edit().putLong(key, value).apply()
            is Float -> sharePref.edit().putFloat(key, value).apply()
        }

    }

    fun getString(key: String, value: String = ""): String {
        return sharePref.getString(key, value)?.trim() ?: value
    }

    private fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharePref.getInt(key, defaultValue)
    }

    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharePref.getBoolean(key, defaultValue)
    }

    fun getLong(key: String): Long {
        return sharePref.getLong(key, 0L)
    }

    fun getFloat(key: String): Float {
        return sharePref.getFloat(key, 0f)
    }


    /*-----------------------------------------Language---------------------------------------------*/
    fun getCodeLanguageChoose(): String = getString("getCodeLanguageChoose", "en")
    fun setCodeLanguageChoose(value: String) = saveKey("getCodeLanguageChoose", value)

    /*----------------------------------------Notification------------------------------------------*/
    fun isFirstRequestNotification(): Boolean = getBoolean("isFirstRequestNotification", true)
    fun setFirstRequestNotification(value: Boolean) = saveKey("isFirstRequestNotification", value)



}