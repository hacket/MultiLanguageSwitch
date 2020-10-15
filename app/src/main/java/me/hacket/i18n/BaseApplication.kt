package me.hacket.i18n

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import me.hacket.i18n.utils.GlobalContext
import me.hacket.i18n.utils.MultiLangUtils

class BaseApplication : Application() {

    companion object {
        const val TAG = "locale"
    }

    override fun onCreate() {
        super.onCreate()
//        MultiLangUtils.applyLanguage(this, MultiLangUtils.getUserSettingLocale(this))
        GlobalContext.initApplication(this)
        GlobalContext.initContext(this)

        MultiLangUtils.init(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        Log.i(TAG, "BaseApplication onConfigurationChanged newConfig=${newConfig}")
        MultiLangUtils.applyLanguage(this, MultiLangUtils.getUserSettingLocale(this))
        super.onConfigurationChanged(newConfig)
    }

}