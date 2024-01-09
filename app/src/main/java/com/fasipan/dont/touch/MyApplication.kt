package com.fasipan.dont.touch

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.service.LockActivity
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import com.fasipan.dont.touch.utils.data.DataAudioUtils

class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

    companion object {

    }

    fun finishActivity() {
        if (currentActivity is LockActivity) {
            currentActivity?.finish()
        }
    }

    fun isLockActivity(): Boolean {
        return currentActivity is LockActivity
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        SharePreferenceUtils.init(this)
        LocalDataSource.init(this)
        DataAudioUtils.init(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}