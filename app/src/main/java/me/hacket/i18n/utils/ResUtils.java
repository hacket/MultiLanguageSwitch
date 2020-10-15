package me.hacket.i18n.utils;

import android.content.res.Resources;
import android.graphics.Color;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;

import me.hacket.i18n.utils.GlobalContext;

/**
 * 获取资源
 */
public class ResUtils {

    private static Resources res = GlobalContext.getApplication().getResources();

    public static Resources getRes(){
        return res;
    }

    /**
     * 取得 drawble 类型图片资源
     */
//    public static @Nullable Drawable getDrawable(@DrawableRes int resID) {
//        if (resID == 0) {
//            return null;
//        }
//        return CompatUtil.getDrawable(resID);
//    }

    /**
     * 取得 color
     */
//    public static int getColor(@ColorRes int resID) {
//        return CompatUtil.getColor(resID);
//    }

    /**
     * 取得 String []
     */
    public static String[] getStrArray(@ArrayRes int resID) {
        return GlobalContext.getApplication().getResources().getStringArray(resID);
    }

    /**
     * 取得 String
     */
    public static String getStr(@StringRes int resID) {
        return GlobalContext.getApplication().getResources().getString(resID);
    }

    public static String getStr(@StringRes int id, Object... formatArgs) {
        return GlobalContext.getApplication().getResources().getString(id, formatArgs);
    }

    /**
     * 从资源文件（单位为dip）取得控件大小（单位为pix）
     */
    public static int getDimen(@DimenRes int resID) {
        return GlobalContext.getApplication().getResources().getDimensionPixelSize(resID);
    }

    /**
     * 从资源文件获取颜色状态（包括多种状态）
     */
//    public static ColorStateList getColorStateList(@ColorRes int resID) {
//        return CompatUtil.getColorStateList(resID);
//    }

//    /**
//     * 从res获取bitmap
//     *
//     * @param id drawable id
//     * @return Bitmap
//     */
//    public static Bitmap getBitmap(@DrawableRes int id) {
//        return BitmapFactory.decodeResource(GlobalContext.getAppContext().getResources(), id);
//    }

    @ColorInt
    public static int parseColor(String colorString, @ColorInt int defaultColor) {
        try {
            return Color.parseColor(colorString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return defaultColor;
    }

}
