package com.mobivault.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
 import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
public class Mobivault : Application(), Application.ActivityLifecycleCallbacks {

companion object{
     var context: Context? = null
    private const val TAG = "MobiRecord@@@"


}

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    open fun getAppContext(): Context? {
        return context
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}