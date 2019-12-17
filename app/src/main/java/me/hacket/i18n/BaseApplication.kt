package me.hacket.i18n

import android.app.Application

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleUtils.init(this)
    }
}