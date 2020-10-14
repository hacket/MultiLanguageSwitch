package me.hacket.i18n

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultLocale = Locale.getDefault()
        val locale1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.resources.configuration.locales[0]
        } else {
            this.resources.configuration.locale
        }
        val locale2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            application.resources.configuration.locales[0]
        } else {
            application.resources.configuration.locale
        }
        val locale3 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applicationContext.resources.configuration.locales[0]
        } else {
            applicationContext.resources.configuration.locale
        }
        val locale4 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }
        val locale5 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GlobalContext.getAppContext().resources.configuration.locales[0]
        } else {
            GlobalContext.getAppContext().resources.configuration.locale
        }
        val locale6 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GlobalContext.getApplication().resources.configuration.locales[0]
        } else {
            GlobalContext.getApplication().resources.configuration.locale
        }

        val locale7 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ResUtils.getRes().configuration.locales[0]
        } else {
            ResUtils.getRes().configuration.locale
        }

        val s = """
        系统默认Locale：Locale.getDefault()=$defaultLocale(${defaultLocale.hashCode()})
        Activity#Resource.Locale=$locale1(${locale1.hashCode()})
        application#Resource.Locale=$locale2(${locale2.hashCode()})
        applicationContext#Resource.Locale=$locale3(${locale3.hashCode()})
        Resources.getSystem()#Resource.Locale=$locale4(${locale4.hashCode()})
        GlobalContext.getAppContext()#Resource.Locale=$locale5(${locale5.hashCode()})
        GlobalContext.getApplication()#Resource.Locale=$locale6(${locale6.hashCode()})
        ResUtils.getRes()#Resource.Locale=$locale7(${locale7.hashCode()})
        """.trimMargin()

        tv_locale.text = s
        Log.d("locale", "MainActivity onCreate locale=$s")

        tv_test1.text = ResUtils.getStr(R.string.already_exit_room)
        tv_test2.text = applicationContext.resources.getString(R.string.about)
        tv_test3.text = resources.getString(R.string.all_photos)
        tv_test4.text = GlobalContext.getAppContext().resources.getString(R.string.album)

        btn_new_page.setOnClickListener {
            startActivity(Intent(this, NewPageActivity::class.java))
        }
        btn_get_album.setOnClickListener {
            val s = ResUtils.getStr(R.string.album)
            Toast.makeText(application, s, Toast.LENGTH_SHORT).show()
        }
    }
}
