package com.zyl.mp3cutter.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Description: App context工具
 * Created by zouyulong on 2017/12/4.
 */

public final class AppContextUtils {

    public static synchronized PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager e = getPackageManager(context);
            return e == null?null:e.getPackageInfo(getPackageName(context), 1);
        } catch (NullPointerException | PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static PackageManager getPackageManager(Context context) {
        try {
            return context.getPackageManager();
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception var2) {
            var2.printStackTrace();
            return "";
        }
    }
}

