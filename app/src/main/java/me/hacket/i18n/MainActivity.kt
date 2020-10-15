package me.hacket.i18n

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_content.*
import me.hacket.i18n.utils.*
import java.util.*

class MainActivity : AppCompatActivity() {


    enum class EnumTest(val s: String) {
        ONE(ResUtils.getStr(R.string.title_tab_recommend));
    }

    // 单例
    object ObjTest {
        val name: String = ResUtils.getStr(R.string.title_tab_recommend)
        val name1: String =
            GlobalContext.getAppContext().resources.getString(R.string.title_tab_recommend)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        test()
    }

    private fun test() {

        // WebView，
//        webview.loadUrl("https://www.qq.com")
//        MultiLangUtils.applyLanguage(
//            this,
//            MultiLangUtils.getUserSettingLocale(this),
//            null
//        )

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

        // 单例
//        tv_test5.text = ObjTest.name1
        // 枚举
        tv_test5.text = EnumTest.ONE.s

        // Toast
        btn_get_album.setOnClickListener {
            val s = ResUtils.getStr(R.string.album)
            Toast.makeText(application, s, Toast.LENGTH_SHORT).show()
        }

    }

    private fun init() {
        btn_new_page.setOnClickListener {
            startActivity(Intent(this, NewPageActivity::class.java))
        }
        btn_go_webview.setOnClickListener {
            startActivity(Intent(this, WebActivity::class.java))
        }
    }
}
