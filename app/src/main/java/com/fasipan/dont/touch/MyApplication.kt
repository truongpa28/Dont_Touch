package com.fasipan.dont.touch

import android.app.Application
import com.fasipan.dont.touch.utils.SharePreferenceUtils

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharePreferenceUtils.init(this)
    }

}