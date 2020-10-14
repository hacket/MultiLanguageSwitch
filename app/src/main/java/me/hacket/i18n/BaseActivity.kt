package me.hacket.i18n

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        Log.d(
            BaseApplication.TAG,
            "${this.javaClass.simpleName} attachBaseContext newBase=${newBase}"
        )
        newBase?.let {
//            val locale = MultiLangUtils.getUserSettingLocale(it)
//            MultiLangUtils.changeLanguage(it, locale)
            super.attachBaseContext(newBase)
        }
    }

}
