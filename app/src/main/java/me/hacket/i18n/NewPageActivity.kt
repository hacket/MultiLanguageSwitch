package me.hacket.i18n

import android.content.res.Resources
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

        Log.d("locale", "NewPageActivity onCreate locale=$s")

        btn_change_zh.setOnClickListener {
//            LanguageUtils.applyLanguage(Locale.CHINA, "me.hacket.i18n.MainActivity")

//            if (MultiLangUtils.needUpdateLocale(this, Locale.SIMPLIFIED_CHINESE)) {
//                val context =
//                    MultiLangUtils.changeLanguage(this, Locale.SIMPLIFIED_CHINESE)
//                MultiLangUtils.changeLanguage(applicationContext, Locale.SIMPLIFIED_CHINESE)
//                GlobalContext.initContext(context)
//                MultiLangUtils.restartAct(this, "me.hacket.i18n.MainActivity")
//            }


//            if (LocaleUtils.updateLocale(this, Locale.SIMPLIFIED_CHINESE)) {
//                LocaleUtils.restartAct(this, MainActivity::class.java)
//            }

            MultiLangUtils.applyLanguage(this, Locale.CHINA, "me.hacket.i18n.MainActivity")
        }
        btn_change_en.setOnClickListener {

//            LanguageUtils.applyLanguage(Locale.US, "me.hacket.i18n.MainActivity")

//            if (LocaleUtils.updateLocale(this, Locale.ENGLISH)) {
//                LocaleUtils.restartAct(this, MainActivity::class.java)
//            }

//            if (MultiLangUtils.needUpdateLocale(this, Locale.ENGLISH)) {
//                val context = MultiLangUtils.changeLanguage(this, Locale.ENGLISH)
//                MultiLangUtils.changeLanguage(applicationContext, Locale.ENGLISH)
//                GlobalContext.initContext(context)
//                MultiLangUtils.restartAct(this, "me.hacket.i18n.MainActivity")
//            }

            MultiLangUtils.applyLanguage(this, Locale.ENGLISH, "me.hacket.i18n.MainActivity")
        }
        btn_change_ar.setOnClickListener {
            MultiLangUtils.applyLanguage(this, Locale("ar", "ar"), "me.hacket.i18n.MainActivity")
        }
        btn_change_auto.setOnClickListener {
//            LanguageUtils.applySystemLanguage("me.hacket.i18n.MainActivity")

//            if (LocaleUtils.updateLocale(this, Locale.getDefault())) {
//                LocaleUtils.restartAct(this, MainActivity::class.java)
//            }

//            if (MultiLangUtils.needUpdateLocale(this, Locale.getDefault())) {
//                val context = MultiLangUtils.changeLanguage(this, Locale.getDefault())
//                MultiLangUtils.changeLanguage(applicationContext, Locale.getDefault())
//                GlobalContext.initContext(context)
//                MultiLangUtils.restartAct(this, "me.hacket.i18n.MainActivity")
//            }


            MultiLangUtils.applySystemLanguage(this, "me.hacket.i18n.MainActivity")
        }
    }
}
