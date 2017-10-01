package com.zyl.mp3cutter.common.utils;

/**
 * Description: mp3cutter.zyl.com.mp3cutter.common.utils
 * Created by zouyulong on 2017/9/26.
 * Leader : lixiao
 */

public class TimeUtils {

    /**
     * 格式化毫秒->00:00
     * */
    public static String formatSecondTime(int millisecond) {
        if (millisecond == 0) {
            return "00:00";
        }
        millisecond = millisecond / 1000;
        int m = millisecond / 60 % 60;
        int s = millisecond % 60;
        return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
    }
}
