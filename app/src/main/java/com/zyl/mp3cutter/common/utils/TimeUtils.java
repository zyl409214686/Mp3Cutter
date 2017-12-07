package com.zyl.mp3cutter.common.utils;

/**
 * Description: mp3cutter.zyl.com.mp3cutter.common.utils
 * Created by zouyulong on 2017/9/26.
 * Leader : lixiao
 */

public class TimeUtils {

    /**
     * 格式化毫秒->00:00
     */
    public static String formatSecondTime(int millisecond) {
        if (millisecond == 0) {
            return "00:00";
        }
        int second = millisecond / 1000;
        int m = second / 60;
        int s = second % 60;
        if (m >= 60) {
            int hour = m / 60;
            int minute = m % 60;
            return hour + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (s > 9 ? s : "0" + s);
        } else {
            return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
        }
    }
}
