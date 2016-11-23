package cn.truistic.enmicromsg.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import cn.truistic.enmicromsg.R;
import cn.truistic.enmicromsg.main.MainMVP;

/**
 * SharedPerferences工具类
 */
public class SharedPerfUtil {

    // 保存密钥
    public static void saveSecret(Context context, String secret) {
        SharedPreferences.Editor editor = getAppSharedPerf(context).edit();
        editor.putString(context.getString(R.string.saved_app_secret), secret);
        editor.commit();
    }

        // 是否第一次启动应用
    public static void saveIsFirstStart(Context context, boolean isFirstStart) {
        SharedPreferences.Editor editor = getAppSharedPerf(context).edit();
        editor.putBoolean(context.getString(R.string.saved_app_is_first_start), isFirstStart);
        editor.commit();
    }

    // 是否第一次启动应用
    public static boolean getIsFirstStart(Context context) {
        return getAppSharedPerf(context).getBoolean(context.getString(R.string.saved_app_is_first_start), true);
    }
    // 密钥
    public static String getSecret(Context context) {
        return getAppSharedPerf(context).getString(context.getString(R.string.saved_app_secret), "");
    }

    // 获取状态
    public static int getState(Context context, MainMVP.IHomeView.Progress progress) {
        SharedPreferences sp = getAppSharedPerf(context);
        return sp.getInt(progress.name(), 0);
    }

    // 保存状态
    public static void saveProgressState(Context context, MainMVP.IHomeView.Progress progress, int state) {
        SharedPreferences.Editor editor = getAppSharedPerf(context).edit();
        editor.putInt(progress.name(), state);
        editor.commit();
    }

    // 获取微信数据库（账号）数量
    public static int getDbNum(Context context) {
        return getDataSharedPerf(context).getInt(context.getString(R.string.saved_db_num), 0);
    }

    // 保存微信数据库（账号）数量
    public static void saveDbNum(Context context, int num) {
        SharedPreferences.Editor editor = getDataSharedPerf(context).edit();
        editor.putInt(context.getString(R.string.saved_db_num), num);
        editor.commit();
    }

    // 获取数据库密码
    public static String getDbPwd(Context context) {
        return getDataSharedPerf(context).getString(context.getString(R.string.saved_db_pwd), null);
    }

    // 保存数据库密码
    public static void savedbPwd(Context context, String pwd) {
        SharedPreferences.Editor editor = getDataSharedPerf(context).edit();
        editor.putString(context.getString(R.string.saved_db_pwd), pwd);
        editor.commit();
    }

    // 获取uin
    public static int getUin(Context context) {
        SharedPreferences sp = context.getSharedPreferences("system_config_prefs", Context.MODE_PRIVATE);
        return sp.getInt("default_uin", 0);
    }

    private static SharedPreferences getAppSharedPerf(Context context) {
        return context.getSharedPreferences(context.getString(R.string.shared_perf_app), Context.MODE_PRIVATE);
    }

    private static SharedPreferences getDataSharedPerf(Context context) {
        return context.getSharedPreferences(context.getString(R.string.shared_perf_data), Context.MODE_PRIVATE);
    }

}
