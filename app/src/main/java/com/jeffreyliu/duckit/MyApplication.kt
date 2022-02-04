package com.jeffreyliu.duckit

import android.app.Application
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs

//import com.orhanobut.logger.AndroidLogAdapter
//import com.orhanobut.logger.Logger.addLogAdapter


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        addLogAdapter(AndroidLogAdapter())

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}