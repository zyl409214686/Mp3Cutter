package com.zyl.mp3cutter.mp3cut.logic;

import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.utils.Md5Utils;
import com.zyl.mp3cutter.home.bean.MusicInfo;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Description: 读取mp3元数据
 * Created by zouyulong on 2018/2/4.
 */

public class Mp3InfoUtils {

    public static void writeMp3Info(String path, FieldKey key, String value) {
        MP3File file;
        try {
            file = new MP3File(path);
            setMetaInfo(file, key, value);
            String songName = getMetaInfo(file, "TIT2");
            System.out.print(songName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }
    }

    public static MusicInfo readMp3Info(String path) {
        MP3File file;
        MusicInfo info = new MusicInfo();
        info.setFilepath(path);
        try {
            file = new MP3File(path);
            String songName = getMetaInfo(file, "TIT2");
            String artist = getMetaInfo(file, "TPE1");
            String album = getMetaInfo(file, "TALB");
//            TagField title = file.getID3v2Tag().getFields(FieldKey.TITLE).get(0);
//            TagField title2 = file.getID3v2Tag().getFirstField(FieldKey.TITLE);
//            TagField title3 = file.getID3v2Tag().getFirstField("TIT2");
            String length = file.getMP3AudioHeader().getTrackLengthAsString();
            if (songName == null)
                return null;
            songName = songName.substring(6, songName.length() - 3);
            if (artist != null)
                artist = artist.substring(6, artist.length() - 3);
            if (album != null)
                album = album.substring(6, album.length() - 3);
            info.setFilename(songName + ".mp3");
            info.setTitle(songName);
            info.setAlbum(album);
            info.setArtist(artist);
            info.setCoverPath(saveMP3Image(new File(path), info.getTitle(),
                    MyApplication.instances.getExternalCacheDir().getAbsolutePath(),
                    false));
//            info.setTrackLength(length);
            return info;
        } catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
            return null;
//            throw new RuntimeException("获取Mp3 tag信息出错！");

        }

//          System.out.println("歌名"+songName);
//          System.out.println("歌手"+singer);
//          System.out.println("专辑:"+author);
    }

    private static String getMetaInfo(MP3File file, String key) {
        if (file.getID3v2Tag() == null || file.getID3v2Tag().frameMap == null
                || file.getID3v2Tag().frameMap.get(key) == null)
            return null;
        return ((AbstractID3v2Frame) file.getID3v2Tag().frameMap.get(key)).getBody().toString();
    }

    public static boolean setMetaInfo(MP3File file, FieldKey key, String value) throws FieldDataInvalidException {
        if (file.getID3v2Tag() == null)
            return false;
        file.getID3v2Tag().setField(key, value);
        return true;
    }

    /**
     * 获取封面路径
     * @param path
     * @param title
     * @return
     */
    public static String getCoverPath(String path, String title){
        return saveMP3Image(new File(path), title, MyApplication.instances.getExternalCacheDir().getAbsolutePath(),
                false);
    }

    /**
     * 获取mp3图片并将其保存至指定路径下
     *
     * @param title         mp3文件对象
     * @param mp3ImageSavePath mp3图片保存位置（默认mp3ImageSavePath +"\" mp3File文件名 +".jpg" ）
     * @param cover            是否覆盖已有图片
     * @return 生成图片全路径
     */
    public static String saveMP3Image(File mp3File, String title, String mp3ImageSavePath, boolean cover) {
        //生成mp3图片路径
        String mp3ImageFullPath = mp3ImageSavePath + "/" + (Md5Utils.md5(title) + ".jpg");

        //若为非覆盖模式，图片存在则直接返回（不再创建）
        if (!cover) {
            File tempFile = new File(mp3ImageFullPath);
            if (tempFile.exists()) {
                return mp3ImageFullPath;
            }
        }

        //生成mp3存放目录
        File saveDirectory = new File(mp3ImageSavePath);
        saveDirectory.mkdirs();

        //获取mp3图片
        byte imageData[] = getMP3Image(mp3File);
        //若图片不存在，则直接返回null
        if (null == imageData || imageData.length == 0) {
            return null;
        }
        //保存mp3图片文件
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mp3ImageFullPath);
            fos.write(imageData);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mp3ImageFullPath;
    }

    /**
     * 获取MP3封面图片
     *
     * @param mp3File
     * @return
     */
    public static byte[] getMP3Image(File mp3File) {
        byte[] imageData = null;
        try {
            MP3File mp3file = new MP3File(mp3File);
            AbstractID3v2Tag tag = mp3file.getID3v2Tag();
            AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
            if(frame!=null) {
                FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
                imageData = body.getImageData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageData;
    }


    /**
     * 仅返回文件名（不包含.类型）
     *
     * @param fileName
     * @return
     */
    private static String getFileLabel(String fileName) {
        int indexOfDot = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, (indexOfDot == -1 ? fileName.length() : indexOfDot));
        return fileName;
    }

    private static String toGB2312(String s) {
        try {
            return new String(s.getBytes("ISO-8859-1"), "gb2312");
        } catch (Exception e) {
            return s;
        }
    }
}
