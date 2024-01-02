package com.fasipan.dont.touch.utils

import android.content.Context
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.model.LanguageModel

object LanguageUtils {

    val listLanguage = listOf<LanguageModel>(
        LanguageModel(R.drawable.ic_flag_gb, "English", "en"),
        LanguageModel(R.drawable.ic_flag_ww, "Arabic", "ar"),
        LanguageModel(R.drawable.ic_flag_ww, "Bangla", "bn"),
        LanguageModel(R.drawable.ic_flag_ww, "Persian", "fa"),
        LanguageModel(R.drawable.ic_flag_fr, "French", "fr"),
        LanguageModel(R.drawable.ic_flag_de, "German", "de"),
        LanguageModel(R.drawable.ic_flag_in, "Hindi", "hi"),
        LanguageModel(R.drawable.ic_flag_id, "Indonesia", "in"),
        LanguageModel(R.drawable.ic_flag_vn, "Vietnamese", "vi"),
    )


    fun getPositionChoose(context: Context): Int {
        //val code = Locale.getDefault().language
        val code = SharePreferenceUtils.getCodeLanguageChoose()
        val value = listLanguage.indexOfFirst { it.key == code }
        return if (value < 0) {
            0
        } else {
            value
        }

    }

}