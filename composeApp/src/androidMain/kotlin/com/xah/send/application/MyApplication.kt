package com.xah.send.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.xah.send.logic.util.android.LogUtil
import java.util.Collections

class MyApplication : Application() {
    companion object {
        lateinit var context : Context
        // App名称
        const val APP_NAME = "Send"
        // Activity栈
        private val activities = Collections.synchronizedList(mutableListOf<Activity>())
        // 安全地退出App
        fun exitAppSafely() {
            activities.toList().forEach { activity ->
                activity.finish()
            }
        }
        // 安全地获取activity
        fun getActivitySafely() : Activity? = activities.toList().first()
    }
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        LogUtil.tag = APP_NAME
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(a: Activity, b: Bundle?) {
                activities.add(a)
            }

            override fun onActivityDestroyed(a: Activity) {
                activities.remove(a)
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}
        })
    }
}