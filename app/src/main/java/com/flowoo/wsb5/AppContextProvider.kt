package com.flowoo.wsb5

import android.app.Application
import android.content.Context

class AppContextProvider : Application() {

    companion object {
        private lateinit var appContext: Context

        fun getAppContext(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
