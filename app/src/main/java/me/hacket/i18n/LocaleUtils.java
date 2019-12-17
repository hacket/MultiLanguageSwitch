package me.hacket.i18n;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Locale;

public class LocaleUtils {

    private static final String TAG = "locale";

    /**
     * 中文
     */
    public static final Locale LOCALE_CHINESE = Locale.CHINESE;
    /**
     * 英文
     */
    public static final Locale LOCALE_ENGLISH = Locale.ENGLISH;

    /**
     * 保存SharedPreferences的文件名
     */
    private static final String LOCALE_FILE = "LOCALE_FILE";
    /**
     * 保存Locale的key
     */
    private static final String LOCALE_KEY = "LOCALE_KEY";

    public static void init(Context context) {
        Locale locale = LocaleUtils.getUserSettingLocale(context);
        LocaleUtils.updateLocale(context, locale);
    }

    /**
     * 获取用户设置的Locale
     *
     * @param context Context
     * @return Locale
     */
    public static Locale getUserSettingLocale(Context context) {
        SharedPreferences sp = context.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        String savedLocaleJson = sp.getString(LOCALE_KEY, "");
        Log.i(TAG, "getUserSettingLocale(获取用户设置的Locale)=" + savedLocaleJson);
        return jsonToLocale(savedLocaleJson);
    }

    /**
     * 获取当前的Locale
     *
     * @param context Context
     * @return Locale
     */
    public static Locale getCurrentLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        Log.i(TAG, "getUserSettingLocale(获取当前的Locale)=" + locale);
        return locale;
    }

    /**
     * 保存用户设置的Locale
     *
     * @param context Context
     * @param locale  Locale
     */
    public static void saveUserSettingLocale(Context context, Locale locale) {
        SharedPreferences sp = context.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String localeToJson = localeToJson(locale);
        edit.putString(LOCALE_KEY, localeToJson).apply();
        Log.w(TAG, "getUserSettingLocale(保存用户设置的Locale)=" + locale);
    }

    /**
     * Locale转成json
     *
     * @param locale UserLocale
     * @return json String
     */
    private static String localeToJson(Locale locale) {
        Gson gson = new Gson();
        String json = gson.toJson(locale);
        return json;
    }

    /**
     * json转成Locale
     *
     * @param pLocaleJson LocaleJson
     * @return Locale
     */
    private static Locale jsonToLocale(String pLocaleJson) {
        Gson gson = new Gson();
        return gson.fromJson(pLocaleJson, Locale.class);
    }

    /**
     * 更新Locale
     *
     * @param context       Context
     * @param newUserLocale New User Locale
     */
    public static boolean updateLocale(Context context, Locale newUserLocale) {
        if (needUpdateLocale(context, newUserLocale)) {
            Configuration configuration = context.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(newUserLocale);
            } else {
                configuration.locale = newUserLocale;
            }
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            context.getResources().updateConfiguration(configuration, metrics);
            saveUserSettingLocale(context, newUserLocale);
            Log.w(TAG, "updateLocale（更新Locale）" + newUserLocale);
            return true;
        }
        return false;
    }

    /**
     * 判断需不需要更新
     *
     * @param context       Context
     * @param newUserLocale New User Locale
     * @return true / false
     */
    public static boolean needUpdateLocale(Context context, Locale newUserLocale) {
        return newUserLocale != null && !getCurrentLocale(context).equals(newUserLocale);
    }

    /**
     * 是否是设置值
     *
     * @return 是否是设置值
     */
    public static boolean isSetValue(Context context) {
        return needUpdateLocale(context, getUserSettingLocale(context));
    }

    /**
     * 重启当前Activity
     */
    public static void restartAct(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
