package me.hacket.i18n.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * GlobalContext <br/>
 *
 * @author zengfansheng <br/>
 * @time 2017/10/24 15:08 <br/>
 * @since v1.0
 */
public class GlobalContext {

    static final String ENV_RELEASE = "release"; // 线上包
    static final String ENV_PREVIEW = "preview"; // 测试包/体验包
    static final String ENV_DEV = "debug"; // 开发包

    private static Context sAppContext;
    private static Application sApp;
    /**
     * log日志
     */
    private static boolean isLogDebug;
    /**
     * debug模式
     */
    private static boolean isDebugMode;

    /**
     * build信息
     */
    private static String buildInfo;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void initContext(Context context) {
        sAppContext = context;
    }

    public static void initApplication(Application application) {
        sApp = application;
    }

    public static void initLogDebug(boolean isLogDebug) {
        GlobalContext.isLogDebug = isLogDebug;
    }

    public static void initDebugMode(boolean isDebugMode) {
        GlobalContext.isDebugMode = isDebugMode;
    }

    public static void initBuildInfo(String buildInfo) {
        GlobalContext.buildInfo = buildInfo;
    }


    public static Context getAppContext() {
        return sAppContext;
    }

    public static Application getApplication() {
        return sApp;
    }

    public static boolean isLogDebug() {
        return isLogDebug;
    }

    public static boolean isDebugMode() {
        return isDebugMode;
    }

    public static String getBuildInfo() {
        return buildInfo;
    }

    public static Handler getHandler() {
        return mHandler;
    }

}
