package com.fasipan.dont.touch.utils

import android.content.ClipData
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


    /*----------------------------------------App Service------------------------------------------*/
    fun isAppServiceEnable(): Boolean = getBoolean("isAppServiceEnable", false)
    fun setAppServiceEnable(value: Boolean) = saveKey("isAppServiceEnable", value)

    fun getPositionAudioChoose() = getInt("getPositionAudioChoose", 1)
    fun setPositionAudioChoose(value: Int) = saveKey("getPositionAudioChoose", value)

    fun getBatteryChanged() = getInt("getBatteryChanged", 1)
    fun setBatteryChanged(value: Int) = saveKey("getBatteryChanged", value)

    fun getPlayDuration() = getInt("getPlayDuration", 0)
    fun setPlayDuration(value: Int) = saveKey("getPlayDuration", value)

    fun getAudioWaring() = getString("getAudioWaring", "")
    fun setAudioWaring(value: String) = saveKey("getAudioWaring", value)

    fun isEnableAudioSound() = getBoolean("isEnableAudioSound", true)
    fun setEnableAudioSound(isEnable: Boolean) = saveKey("isEnableAudioSound", isEnable)

    fun getVolumeAudioSound() = getInt("getVolumeAudioSound", 100)
    fun setVolumeAudioSound(data: Int) = saveKey("getVolumeAudioSound", data)

    fun getSensitivity() = getInt("getSensitivityWaring", 50)
    fun setSensitivity(data: Int) = saveKey("getSensitivityWaring", data)


    /*-----------------------------------------Language---------------------------------------------*/
    fun getCodeLanguageChoose(): String = getString("getCodeLanguageChoose", "en")
    fun setCodeLanguageChoose(value: String) = saveKey("getCodeLanguageChoose", value)

    /*----------------------------------------Notification------------------------------------------*/
    fun isFirstRequestNotification(): Boolean = getBoolean("isFirstRequestNotification", true)
    fun setFirstRequestNotification(value: Boolean) = saveKey("isFirstRequestNotification", value)

    /*---------------------------------------Light up-----------------------------------------------*/
    fun isEnableLightUpMode(): Boolean = getBoolean("isEnableLightUpMode", true)
    fun setEnableLightUpMode(value: Boolean) = saveKey("isEnableLightUpMode", value)

    /*-----------------------------------------Flash------------------------------------------------*/
    fun isEnableFlashMode(): Boolean = getBoolean("isEnableFlashMode", true)
    fun setEnableFlashMode(value: Boolean) = saveKey("isEnableFlashMode", value)

    fun getTypeFlash() = getInt("type_flash", 0)
    fun setTypeFlash(typeFlash: Int) = saveKey("type_flash", typeFlash)

    fun isEnableSpeedMode() = getBoolean("is_enable_speed_mode", true)
    fun setIsEnableSpeedMode(isEnable: Boolean) = saveKey("is_enable_speed_mode", isEnable)


    /*-----------------------------------------Vibrate----------------------------------------------*/
    fun getVibrateRingtone() = getInt("get_vibrate_ringtone", 0)
    fun setVibrateRingtone(value: Int) = saveKey("get_vibrate_ringtone", value)

    fun isEnableVibrate() = getBoolean("is_enable_vibrate", true)
    fun setEnableVibrate(isEnable: Boolean) = saveKey("is_enable_vibrate", isEnable)


    /*-----------------------------------------Wallpaper--------------------------------------------*/
    fun getWallpaperApply() = getInt("getWallpaperApply", -1)
    fun setWallpaperApply(value: Int) = saveKey("getWallpaperApply", value)



    /*--------------------------------------Clap To Find--------------------------------------------*/
    fun isEnableClapToFind(): Boolean = getBoolean("isEnableClapToFind", false)
    fun setEnableClapToFind(value: Boolean) = saveKey("isEnableClapToFind", value)


    /*--------------------------------------Full Battery--------------------------------------------*/
    fun isEnableFullPin(): Boolean = getBoolean("isEnableFullPin", false)
    fun setEnableFullPin(value: Boolean) = saveKey("isEnableFullPin", value)


    /*--------------------------------------Unplug Battery------------------------------------------*/
    fun isEnableUnplugPin(): Boolean = getBoolean("isEnableUnplugPin", false)
    fun setEnableUnplugPin(value: Boolean) = saveKey("isEnableUnplugPin", value)


}