package com.fasipan.dont.touch

import android.app.Application
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.DataAudioUtils

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharePreferenceUtils.init(this)
        LocalDataSource.init(this)
        DataAudioUtils.init(this)
    }

}