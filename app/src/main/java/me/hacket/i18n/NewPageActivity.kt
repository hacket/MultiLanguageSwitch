package me.hacket.i18n

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_page.*
import kotlinx.android.synthetic.main.layout_content.*
import me.hacket.i18n.utils.*
import java.util.*

class NewPageActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_page)

//        toolbar.title = resources.getString(R.string.title_setting)
//        toolbar.background = ColorDrawable(resources.getColor(R.color.colorPrimary))

        btn_change_zh.setOnClickListener {
            MultiLangUtils.applyLanguage(this, Locale.CHINA, "me.hacket.i18n.MainActivity", true)
        }
        btn_change_en.setOnClickListener {
            MultiLangUtils.applyLanguage(this, Locale.US, "me.hacket.i18n.MainActivity", true)
        }
        btn_change_ar.setOnClickListener {
            MultiLangUtils.applyLanguage(this, arLocale(), "me.hacket.i18n.MainActivity", true)
        }
        btn_change_fr.setOnClickListener {
            MultiLangUtils.applyLanguage(this, Locale.FRANCE, "me.hacket.i18n.MainActivity", true)
        }
        btn_change_auto.setOnClickListener {
            MultiLangUtils.applySystemLanguage(this, "me.hacket.i18n.MainActivity")
        }

        test()
    }

    private fun test() {

        val defaultLocale = Locale.getDefault()
        val locale1 = this.getLocale()
        val locale2 = application.getLocale()
        val locale3 = applicationContext.getLocale()
        val locale4 = getSystemLocale()
        val locale5 = GlobalContext.getAppContext().getLocale()
        val locale6 = GlobalContext.getApplication().getLocale()
        val locale7 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ResUtils.getRes().configuration.locales[0]
        } else {
            ResUtils.getRes().configuration.locale
        }

        val localeText = """
        Locale.getDefault()#Locale=$defaultLocale(${defaultLocale.hashCode()})
        Activity#Locale=$locale1(${locale1.hashCode()})
        Activity#getSysPreferredLocale=${getSysPreferredLocale(this)}
        application#Locale=$locale2(${locale2.hashCode()})
        applicationContext#Locale=$locale3(${locale3.hashCode()})
        Resources.getSystem()#Locale=$locale4(${locale4.hashCode()})
        GlobalContext.getAppContext()#Locale=$locale5(${locale5.hashCode()})
        GlobalContext.getApplication()#Locale=$locale6(${locale6.hashCode()})
        ResUtils.getRes()#Locale=$locale7(${locale7.hashCode()})
        """.trimMargin()
        Log.d("locale", "MainActivity onCreate locale=$localeText")
        tv_locale.text = localeText

        // ResUtils
        tv_test1.text = ResUtils.getStr(R.string.already_exit_room)
        // applicationContext
        tv_test2.text = applicationContext.resources.getString(R.string.about)
        // Activity
        tv_test3.text = resources.getString(R.string.all_photos)
        // GlobalContext
        tv_test4.text = GlobalContext.getAppContext().resources.getString(R.string.album)
        // Toast
        btn_get_album.setOnClickListener {
            val s = ResUtils.getStr(R.string.album)
            Toast.makeText(application, s, Toast.LENGTH_SHORT).show()
        }

        // WebView
//        webview.loadUrl("https://www.qq.com")
//        MultiLangUtils.applyLanguage(
//            this,
//            MultiLangUtils.getUserSettingLocale(this),
//            null
//        )
    }
}
