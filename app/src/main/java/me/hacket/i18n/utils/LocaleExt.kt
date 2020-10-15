package me.hacket.i18n.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
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