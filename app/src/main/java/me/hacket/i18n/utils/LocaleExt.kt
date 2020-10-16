package me.hacket.i18n.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.*


const val LANGUAGE_AR = "ar"
const val LANGUAGE_EN = "en"
const val LANGUAGE_FR = "fr"
const val LANGUAGE_TR = "tr"
const val LANGUAGE_ZH = "zh"

const val COUNTRY_AR = "AR"
const val COUNTRY_TR = "TR"

fun Locale?.isSameLocale(other: Locale?): Boolean {
    if (this == null || other == null) return false
    return other.language == language && other.country == country
}

fun Locale?.isSameLanguage(other: Locale?): Boolean {
    if (this == null || other == null) return false
    return other.language == language
}

// 阿拉伯语/阿拉伯
fun arLocale(): Locale {
    return Locale(LANGUAGE_AR, COUNTRY_AR)
}

// 土耳其语/土耳其
fun trLocale(): Locale {
    return Locale(LANGUAGE_TR, COUNTRY_TR)
}

fun Context.getLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        resources.configuration.locale
    }
}

/**
 * 获取系统首选语言
 *
 * @return
 */
fun getSysPreferredLocale(context: Context): Locale? {
    val locale: Locale
    //7.0以下直接获取系统默认语言
    locale = if (Build.VERSION.SDK_INT < 24) {
        // 等同于context.getResources().getConfiguration().locale;
        Locale.getDefault()
        // 7.0以上获取系统首选语言
    } else {
        val defaultLocaleList = LocaleList.getDefault()
        val sb0 = StringBuffer()
        for (i in 0 until defaultLocaleList.size()) {
            sb0.append(defaultLocaleList[i])
            sb0.append(",")
        }
        Log.d("locale", "LocaleList.getDefault()        : $sb0")
        val list: LocaleList = context.resources.configuration.locales
        val sb1 = StringBuffer()
        for (i in 0 until list.size()) {
            sb1.append(list[i])
            sb1.append(",")
        }
        Log.d("locale", "Configuration.getLocales()     : $sb1")
        val adjustedDefaultLocaleList = LocaleList.getAdjustedDefault()
        val sb2 = StringBuffer()
        for (i in 0 until adjustedDefaultLocaleList.size()) {
            sb2.append(adjustedDefaultLocaleList[i])
            sb2.append(",")
        }
        Log.d("locale", "LocaleList.getAdjustedDefault(): $sb2")
        defaultLocaleList[0]
    }
    return locale
}


fun getCurrentLocale(context: Context? = null): Locale {
    val ctx = context ?: GlobalContext.getAppContext()
    return ctx.getLocale()
}

fun getCurrentLang(context: Context? = null): String {
    val ctx = context ?: GlobalContext.getAppContext()
    return ctx.getLocale().language
}

fun getSystemLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Resources.getSystem().configuration.locales[0]
    } else {
        Resources.getSystem().configuration.locale
    }
}