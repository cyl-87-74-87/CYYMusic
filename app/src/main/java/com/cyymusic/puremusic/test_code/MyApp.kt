package com.cyymusic.puremusic.test_code

import android.app.Activity
import android.app.Application
import android.os.Bundle

// 这样也能保证 一致性，但是笨重了
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // AOP思想
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onActivityStarted(activity: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivityResumed(activity: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivityPaused(activity: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivityStopped(activity: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                TODO("Not yet implemented")
            }

            override fun onActivityDestroyed(activity: Activity) {
                TODO("Not yet implemented")
            }

        })
    }

}