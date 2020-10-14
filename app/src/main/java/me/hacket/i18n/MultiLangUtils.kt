package me.hacket.i18n

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.DisplayMetrics
import android.util.Log
import com.google.gson.Gson
import java.util.*


object MultiLangUtils {

    private const val TAG = "locale"

    /**
     * 保存SharedPreferences的文件名
     */
    private const val LOCALE_FILE = "LOCALE_FILE"

    /**
     * 保存Locale的key
     */
    private const val LOCALE_KEY = "LOCALE_KEY"

    private val gson = Gson()

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : EmptyActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onActivityCreated(activity, savedInstanceState)
                applyLanguage(activity, getUserSettingLocale(activity))
            }
        })
    }

    /**
     * 获取系统的Locale
     */
    private fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0有多语言设置获取顶部的语言
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }
    }

    /**
     * 获取用户设置的Locale
     *
     * @param context Context
     * @return Locale
     */
    @JvmStatic
    fun getUserSettingLocale(context: Context): Locale {
        val sp = context.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE)
        val savedLocaleJson = sp.getString(LOCALE_KEY, "")
        if (savedLocaleJson.isNullOrBlank()) {
            return Locale.getDefault()
        }
        Log.d(TAG, "LocaleSwitchUtils#getUserSettingLocale(获取用户设置的Locale)=$savedLocaleJson")
        return jsonToLocale(savedLocaleJson)
    }

    /**
     * 获取当前的Locale
     *
     * @param context Context
     * @return Locale
     */
    private fun getCurrentLocale(context: Context): Locale {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0有多语言设置获取顶部的语言
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
        Log.d(TAG, "LocaleSwitchUtils#getCurrentLocale(获取当前的Locale)=$locale")
        return locale
    }

    /**
     * 保存用户设置的Locale
     *
     * @param context Context
     * @param locale  Locale
     */
    private fun saveUserSettingLocale(context: Context, locale: Locale) {
        val sp =
            context.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE)
        val edit = sp.edit()
        val localeToJson = localeToJson(locale)
        Log.w(TAG, "LocaleSwitchUtils#saveUserSettingLocale(保存用户设置的Locale)=$locale")
        return edit.putString(LOCALE_KEY, localeToJson).apply()
    }

    /**
     * Locale转成json
     *
     * @param locale UserLocale
     * @return json String
     */
    private fun localeToJson(locale: Locale): String {
        return gson.toJson(locale)
    }

    /**
     * json转成Locale
     *
     * @param pLocaleJson LocaleJson
     * @return Locale
     */
    private fun jsonToLocale(pLocaleJson: String?): Locale {
        return gson.fromJson(pLocaleJson, Locale::class.java)
    }

    fun applySystemLanguage(context: Context, activityClassName: String? = null) {
        val systemLocale = getSystemLocale()
        if (!needUpdateLocale(context, systemLocale)) {
            return
        }
        changeLanguage(context, systemLocale)
        changeLanguage(context.applicationContext, systemLocale)
        saveUserSettingLocale(context, systemLocale)
        if (!activityClassName.isNullOrBlank()) {
            restartActivity(context, activityClassName)
        }
    }

    fun applyLanguage(context: Context, newUserLocale: Locale, activityClassName: String? = null) {
        if (!needUpdateLocale(context, newUserLocale)) {
            return
        }
        changeLanguage(context, newUserLocale)
        changeLanguage(context.applicationContext, newUserLocale)
        saveUserSettingLocale(context, newUserLocale)
        if (!activityClassName.isNullOrBlank()) {
            restartActivity(context, activityClassName)
        }
    }

    /**
     * 设置语言类型
     */
    fun changeLanguage(context: Context, newUserLocale: Locale) {
        val resources: Resources = context.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        Log.e(TAG, "changeLanguage修改语言为:" + newUserLocale.language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(newUserLocale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            Locale.setDefault(newUserLocale)
            resources.updateConfiguration(config, dm) // 不加这句切换不成功
            context.createConfigurationContext(config)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(newUserLocale)
            } else {
                config.locale = newUserLocale
            }
            resources.updateConfiguration(config, dm)
        }
    }

    /**
     * 更新Locale
     *
     * @param context       Context
     * @param newUserLocale New User Locale
     */
    @JvmStatic
    fun updateLocale(context: Context, newUserLocale: Locale): Boolean {
        if (needUpdateLocale(context, newUserLocale)) {
            val resources = context.resources
            val config: Configuration = resources.configuration
            val dm: DisplayMetrics = resources.displayMetrics
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(newUserLocale)
                if (context is Application) {
                    val newContext = context.createConfigurationContext(config)
                    try {
                        val mBaseField = ContextWrapper::class.java.getDeclaredField("mBase")
                        mBaseField.isAccessible = true
                        mBaseField[context] = newContext
                    } catch (ignored: Exception) { /**/
                    }
                }
            } else {
                config.locale = newUserLocale
            }
            resources.updateConfiguration(config, dm)
            Log.w(TAG, "LocaleSwitchUtils#updateLocale（更新Locale）$newUserLocale")
//            ResUtils.updateRes(resources)
            saveUserSettingLocale(context, newUserLocale)
            return true
        }
        return false
    }

    /**
     * 判断需不需要更新
     *
     * @param context       Context
     * @param newUserLocale New User Locale
     * @return true / false
     */
    private fun needUpdateLocale(context: Context, newUserLocale: Locale?): Boolean {
        return newUserLocale != null && getCurrentLocale(context) != newUserLocale
    }

    /**
     * 是否是设置值
     *
     * @return 是否是设置值
     */
    fun isSetValue(context: Context): Boolean {
        return needUpdateLocale(context, getUserSettingLocale(context))
    }

    /**
     * 重启当前Activity
     */
    private fun restartActivity(context: Context, clazz: Class<out Activity?>?) {
        val intent = Intent(context, clazz)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    /**
     * 重启当前Activity
     */
    private fun restartActivity(context: Context, activityClassName: String) {
        val intent = Intent()
        intent.component = ComponentName(context, activityClassName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

}