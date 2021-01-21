package me.hacket.i18n.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.DisplayMetrics
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import java.util.*

object MultiLangUtils {

    const val TAG = "locale"

    /**
     * 保存SharedPreferences的文件名
     */
    private const val LOCALE_FILE = "LOCALE_FILE"

    /**
     * 保存Locale的key
     */
    private const val LOCALE_KEY = "LOCALE_KEY"

    private val gson = Gson()

    @JvmStatic
    fun init(app: Application) {
        val userSettingLocale = getUserSettingLocale(app)
        if (needUpdateLocale(app, userSettingLocale)) {
            LogUtils.d(TAG, "init 需要更新语言为(${userSettingLocale})， app=$app")
            changeLanguage(app, userSettingLocale)
        } else {
            LogUtils.w(TAG, "init 不需要更新语言当前语言(${userSettingLocale}), app=$app")
        }
        app.registerActivityLifecycleCallbacks(object :
            EmptyActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onActivityCreated(activity, savedInstanceState)
                LogUtils.d(TAG, "------>>onActivityCreated context=$activity, locale=${activity.getLocale()}")
                val settingLocale = getUserSettingLocale(activity)
                if (!needUpdateLocale(activity, settingLocale)) {
                    LogUtils.w(TAG, "------>>onActivityCreated applyLanguage 不需要更新语言当前语言为(${settingLocale}), context=$activity")
                } else {
                    LogUtils.d(TAG, "------>>onActivityCreated applyLanguage 需要更新语言为(${settingLocale}), context=$activity")
                    changeLanguage(activity, settingLocale)
                    LogUtils.d(TAG, "------>>onActivityCreated 需要更新语言后，locale=${activity.getLocale()}, userSettingLocale=${getUserSettingLocale(activity)}")
                }
            }

            // 防御性设置，防止其他原因把Locale重置了
            override fun onActivityResumed(activity: Activity) {
                super.onActivityResumed(activity)
                LogUtils.d(TAG, "------->>onActivityResumed context=$activity, locale=${activity.getLocale()}")
                val settingLocale = getUserSettingLocale(activity)
                if (!needUpdateLocale(activity, settingLocale)) {
                    LogUtils.w(TAG, "------->>onActivityResumed applyLanguage 不需要更新语言当前语言为(${settingLocale}), context=$activity")
                } else {
                    LogUtils.i(TAG, "------->>onActivityResumed applyLanguage 需要更新语言为(${settingLocale}), context=$activity")
                    changeLanguage(activity, settingLocale)
                    LogUtils.i(TAG, "------->>onActivityResumed 需要更新语言后，locale=${activity.getLocale()}, userSettingLocale=${getUserSettingLocale(activity)}")
                }
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
            LogUtils.w(TAG, "LocaleSwitchUtils#getUserSettingLocale(savedLocaleJson=null)，获取getCurrentLocale(${getCurrentLocale(context)}), context=$context")
            return getCurrentLocale(context)
        }
        LogUtils.d(TAG, "LocaleSwitchUtils#getUserSettingLocale(获取用户设置的Locale)=$savedLocaleJson, context=$context")
        return jsonToLocale(savedLocaleJson)
    }

    /**
     * 获取当前的Locale
     *
     * @param context Context
     * @return Locale
     */
    fun getCurrentLocale(context: Context): Locale {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0有多语言设置获取顶部的语言
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
        LogUtils.d(TAG, "LocaleSwitchUtils#getCurrentLocale(获取当前的Locale)=$locale, context=$context")
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
        val localeToJson =
            localeToJson(locale)
        val isSuccess =  edit.putString(LOCALE_KEY, localeToJson).commit()
        LogUtils.e(TAG, "LocaleSwitchUtils#saveUserSettingLocale(保存用户设置的Locale)=$locale, context=$context， isSuccess=$isSuccess")
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
        if (!needUpdateLocale(context, systemLocale)
            && !needUpdateLocale(context.applicationContext, systemLocale)
        ) {
            return
        }
        changeLanguage(
            context.applicationContext,
            systemLocale
        )
        changeLanguage(context, systemLocale)
        saveUserSettingLocale(
            context,
            systemLocale
        )
        if (!activityClassName.isNullOrBlank()) {
            restartActivity(
                context,
                activityClassName
            )
        }
    }

    @JvmStatic
    fun applyLanguage(context: Context, newUserLocale: Locale, activityClassName: String? = null, saveConfig: Boolean = false) {
        if (!needUpdateLocale(context, newUserLocale)
            && !needUpdateLocale(context.applicationContext, newUserLocale)
        ) {
            LogUtils.w(TAG, "applyLanguage 不需要更新语言 ${getUserSettingLocale(context)}, context=$context")
            return
        }

        changeLanguage(
            context.applicationContext,
            newUserLocale
        )

        changeLanguage(
            context,
            newUserLocale
        )

        if (saveConfig) {
            saveUserSettingLocale(
                context,
                newUserLocale
            )
        }

        if (!activityClassName.isNullOrBlank()) {
            restartActivity(
                context,
                activityClassName
            )
        }
    }

    /**
     * 设置语言类型
     */
    fun changeLanguage(context: Context, newUserLocale: Locale) {
        val resources: Resources = context.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        LogUtils.e(TAG, "changeLanguage修改语言为:${newUserLocale}，context=${context}")
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
        LogUtils.e(TAG, "changeLanguage语言后当前语言为：${getCurrentLocale(context)}，期望语言为：${newUserLocale}，context=${context}")
    }

    /**
     * 判断需不需要更新
     *
     * @param context       Context
     * @param newUserLocale New User Locale
     * @return true / false
     */
    private fun needUpdateLocale(context: Context, newUserLocale: Locale?): Boolean {
        return newUserLocale != null && !getCurrentLocale(context).isSameLanguage(newUserLocale)
    }

    /**
     * 是否是设置值
     *
     * @return 是否是设置值
     */
    fun isSetValue(context: Context): Boolean {
        return needUpdateLocale(
            context,
            getUserSettingLocale(context)
        )
    }

    /**
     * 重启当前Activity
     */
    private fun restartActivity(context: Context, clazz: Class<out Activity?>?) {
        val intent = Intent(context, clazz)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    /**
     * 重启当前Activity
     */
    private fun restartActivity(context: Context, activityClassName: String) {
        val intent = Intent()
        intent.component = ComponentName(context, activityClassName)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}