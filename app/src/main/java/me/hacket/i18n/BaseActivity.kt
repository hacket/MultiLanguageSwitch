package me.hacket.i18n

import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.i(BaseApplication.TAG, "BaseActivity onConfigurationChanged newConfig=${newConfig}")
        super.onConfigurationChanged(newConfig)
    }
}
