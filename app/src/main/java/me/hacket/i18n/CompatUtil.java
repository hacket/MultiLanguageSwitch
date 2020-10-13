package me.hacket.i18n;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 兼容性工具包 <br/>
 *
 * @author zengfansheng <br/>
 * @time 2017/10/27 11:59 <br/>
 * @since v1.0
 */
public class CompatUtil {

    private static final String TAG = "CompatUtil";

    public static void setBackground(Context context, @NonNull View view, @DrawableRes int drawable) {
        setBackground(view, getDrawable(context, drawable));
    }

    public static void setBackground(@NonNull View view, Drawable drawable) {
        if (view != null) {
            // android4.1
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(drawable);
            } else {
                view.setBackground(drawable);
            }
        }
    }

    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        return ContextCompat.getDrawable(context, id);
    }

    public static int getColor(Context context, @ColorRes int id) {
        return ContextCompat.getColor(context, id);
    }

    public static ColorStateList getColorStateList(Context context, @ColorRes int id) {
        return ContextCompat.getColorStateList(context, id);
    }

    /**
     * 剪切板
     * <p>
     * 部分手机可能会复制失败，没有权限，复制成功返回true
     *
     * @param data data
     */
    public static boolean copyToClipboard(Context context, String data) {
        try {
            if (!TextUtils.isEmpty(data)) {
                int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager cm =
                            (android.text.ClipboardManager) context.getSystemService(
                                    Context.CLIPBOARD_SERVICE);
                    cm.setText(data);
                } else {
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) context.getSystemService(
                                    Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("", data);
                    clipboard.setPrimaryClip(clip);
                }
                return true;
            }
        } catch (SecurityException se) {
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 获取支持的cpuabis
     *
     * @return List<String>
     */
    public static List<String> getSupportedABIS() {
        List<String> supportAbis = new ArrayList<>();
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.LOLLIPOP) {
            String cpuAbi = Build.CPU_ABI;
            String cpuAbi2 = Build.CPU_ABI2;
            if (!TextUtils.isEmpty(cpuAbi)) {
                supportAbis.add(cpuAbi);
            }
            if (!TextUtils.isEmpty(cpuAbi2)) {
                supportAbis.add(cpuAbi2);
            }
        } else {
            supportAbis.addAll(Arrays.asList(Build.SUPPORTED_ABIS));
        }
        return supportAbis;
    }

    /**
     * 是否有通知权限
     */
    public static boolean canNotify(Context context) {
        return NotificationManagerCompat.from(context)
                .areNotificationsEnabled();
    }

    public static CharSequence fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT); // html块元素之间使用两个换行符分隔
        } else {
            return Html.fromHtml(source);
        }
    }

    public static String getProcessName(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        } else {
            return getCurrentProcessName(context);
        }
    }

    private static String getCurrentProcessName(Context context) {
        int mypid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos == null || infos.isEmpty()) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == mypid) {
                return info.processName;
            }
        }
        // may never return null
        return null;
    }

    public static byte[] getAppSignature(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                Signature[] signs = packageInfo.signingInfo.getApkContentsSigners();
                Signature sign = signs[0];
                Log.e(TAG, "getAppSignature,api大于等于28:" + Build.VERSION.SDK_INT + "，Signature" + sign.toCharsString());
                return sign.toByteArray();
            } else {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
                Signature[] signs = packageInfo.signatures;
                Signature sign = signs[0];
                Log.w(TAG, "getAppSignature,api小于28:" + Build.VERSION.SDK_INT + "，Signature" + sign.toCharsString());
                return sign.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAppSignatureSha1(Context context) {
        try {
            byte[] cert = getAppSignature(context);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
            }
            String result = hexString.toString();
            return result.substring(0, result.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Deprecated
    public static String getAppSignatureHex(Context context) {
        byte[] appSignature = getAppSignature(context);
        if (appSignature == null) {
            return "";
        }
        return bytesToHex(appSignature);
    }

    /**
     * Converts byte array to hexidecimal useful for logging and fault finding
     */
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}