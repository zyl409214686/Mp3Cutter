package com.zyl.mp3cutter.common.utils;

import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Description: com.zyl.mp3cutter.common.utils
 * Created by zouyulong on 2018/2/2.
 */

public class SystemUtils {
    public static boolean isFlyme() {
        String flymeFlag = getSystemProperty("ro.build.display.id");
        return !TextUtils.isEmpty(flymeFlag) && flymeFlag.toLowerCase().contains("flyme");
    }

    private static String getSystemProperty(String key) {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            return (String) getMethod.invoke(classType, key);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }
}
