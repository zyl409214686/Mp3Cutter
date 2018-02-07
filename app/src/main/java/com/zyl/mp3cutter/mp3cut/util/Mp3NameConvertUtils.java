package com.zyl.mp3cutter.mp3cut.util;

/**
 * Description: title(无后缀)转为name
 * Created by zouyulong on 2018/2/5.
 */

public class Mp3NameConvertUtils {
    private static final String SUFFIX_LOWER = ".mp3";
    public static String titleConvertName(String title){
        if(title==null)
            return null;
        return title.concat(SUFFIX_LOWER);
    }
}
