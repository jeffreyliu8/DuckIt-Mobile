package com.jeffreyliu.duckit

import android.app.Application
import android.content.ContextWrapper
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        Logger.addLogAdapter(AndroidLogAdapter())
    }
}