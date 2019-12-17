package me.hacket.i18n

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (LocaleUtils.updateLocale(this, LocaleUtils.getUserSettingLocale(this))) {
            LocaleUtils.restartAct(this, MainActivity::class.java)
            finish()
        }
    }
}
