package me.hacket.i18n

import android.os.Build
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_new_page.*
import java.util.*

class NewPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_page)

        val defaultLocale = Locale.getDefault()
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }

        val s = "Locale.getDefault()=$defaultLocale，locale=$locale"
        tv_locale.text = s

        Log.i("locale", "NewPageActivity onCreate locale=$s")

        btn_change_zh.setOnClickListener {
            if (LocaleUtils.updateLocale(this, Locale.SIMPLIFIED_CHINESE)) {
                LocaleUtils.restartAct(this, MainActivity::class.java)
            }
        }
        btn_change_en.setOnClickListener {
            if (LocaleUtils.updateLocale(this, Locale.ENGLISH)) {
                LocaleUtils.restartAct(this, MainActivity::class.java)
            }
        }
        btn_change_auto.setOnClickListener {
            if (LocaleUtils.updateLocale(this, Locale.getDefault())) {
                LocaleUtils.restartAct(this, MainActivity::class.java)
            }
        }
    }
}
