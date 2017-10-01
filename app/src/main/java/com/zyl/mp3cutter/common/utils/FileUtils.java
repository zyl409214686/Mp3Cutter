package com.zyl.mp3cutter.common.utils;

import java.io.File;

/**
 * Description: mp3cutter.zyl.com.mp3cutter.common.utils
 * Created by zouyulong on 2017/9/26.
 * Leader : lixiao
 */

public class FileUtils {

    /**
     * 监测文件夹是否存在，不存在则创建
     * @param strFolder
     * @return
     */
    public static boolean bFolder(String strFolder)
    {
        boolean btmp = false;
        File f = new File(strFolder);
        if (!f.exists())
        {
            if (f.mkdirs())
            {
                btmp = true;
            }
            else
            {
                btmp = false;
            }
        }
        else
        {
            btmp = true;
        }
        return btmp;
    }
}
