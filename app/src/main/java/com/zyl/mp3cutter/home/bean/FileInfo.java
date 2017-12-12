package com.zyl.mp3cutter.home.bean;

import java.util.ArrayList;

/**
 * Description: 文件信息
 * Created by zouyulong on 2017/12/12.
 */

public class FileInfo {
    private FileType fileType;
    private String fileName;
    private String filePath;

    public FileInfo(String filePath, String fileName, boolean isDirectory) {
        this.filePath = filePath;
        this.fileName = fileName;
        fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
    }

    public boolean isMUSICFile() {
        if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
            return false;
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        if (!isDirectory() && MUSIC_SUFFIX.contains(fileSuffix))
            return true;
        else
            return false;
    }

    public boolean isDirectory() {
        if (fileType == FileType.DIRECTORY)
            return true;
        else
            return false;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "FileInfo [fileType=" + fileType + ", fileName=" + fileName
                + ", filePath=" + filePath + "]";
    }

    private static ArrayList<String> MUSIC_SUFFIX = new ArrayList<String>();

    static {
        MUSIC_SUFFIX.add(".mp3");
        // PPT_SUFFIX.add(".pptx");
    }

    enum FileType {
        FILE, DIRECTORY
    }
}
