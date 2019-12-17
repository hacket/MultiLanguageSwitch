package me.hacket.i18n

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultLocale = Locale.getDefault()
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }

        val s = "Locale.getDefault()=$defaultLocale，locale=$locale"
        tv_locale.text = s

        Log.d("locale", "MainActivity onCreate locale=$s")

        btn_new_page.setOnClickListener {
            startActivity(Intent(this, NewPageActivity::class.java))
        }
    }
}
