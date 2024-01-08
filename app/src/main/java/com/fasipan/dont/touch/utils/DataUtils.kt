package com.fasipan.dont.touch.utils

import android.content.Context
import android.os.Environment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataUtils {

    fun getNewPathFile(mContext: Context) : String {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val fname = sdf.format(Date())
        return Environment.getExternalStorageDirectory().absoluteFile.toString() + "/Android/data/" + mContext.packageName +
                "/audio_recording_${fname}.mp3"
    }

    fun getTimeShowFormCount(count : Int) : String {
        return String.format("%02d:%02d", count/60, count%60)
    }

}