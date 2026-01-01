package com.xah.send.application

import android.app.Application
import android.content.Context
import com.xah.send.logic.util.LogUtil

class MyApplication : Application() {
    companion object {
        lateinit var context : Context
    }
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        LogUtil.tag = "Send"
    }
}