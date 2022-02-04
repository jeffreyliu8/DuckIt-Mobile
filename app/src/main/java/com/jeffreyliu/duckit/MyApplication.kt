package com.jeffreyliu.duckit

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger.addLogAdapter


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        addLogAdapter(AndroidLogAdapter())
    }
}