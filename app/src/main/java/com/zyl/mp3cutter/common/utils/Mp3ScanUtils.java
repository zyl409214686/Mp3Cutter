package com.zyl.mp3cutter.common.utils;

import com.zyl.mp3cutter.home.bean.FileInfo;
import com.zyl.mp3cutter.home.bean.MusicInfo;
import com.zyl.mp3cutter.mp3cut.logic.Mp3InfoUtils;

import java.io.File;
import java.util.List;

/**
 * Description: mp3扫描工具类
 * Created by zouyulong on 2018/2/2.
 */

public class Mp3ScanUtils {
    private static final String TAG = "Mp3Utils";
//    private List<MusicInfo> musiclist = new ArrayList<>();
    /**
     * 扫描mp3文件 递归方式
     *
     * @param list
     * @param filePath
     */
    public static void scanMp3File(List<MusicInfo> list, String filePath) throws Exception {
        File[] files = folderScan(filePath);
        if (files == null)
            return;
        File file;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isHidden())
                continue;
            String fileAbsolutePath = files[i].getAbsolutePath();
            String fileName = files[i].getName();
            boolean isDirectory = false;
            if (files[i].isDirectory()) {
                isDirectory = true;
            }
            FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName,
                    isDirectory);
            if (fileInfo.isDirectory())
                scanMp3File(list, fileInfo.getFilePath());
            else if (fileInfo.isMUSICFile()) {
                MusicInfo music = new MusicInfo();
                music.setFilepath(fileInfo.getFilePath());
                music.setTitle(FileUtils.getFileTitle(fileInfo.getFileName()));
                music.setFilename(fileInfo.getFileName());
                music.setCoverPath(Mp3InfoUtils.getCoverPath(music.getFilepath(), music.getTitle()));
                String path = fileInfo.getFilePath();
                file = new File(path);
                long size = FileUtils.getFileSizes(file);
                music.setFileSize(size);
                list.add(music);
            }
        }
    }

    private static File[] folderScan(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }

//    public static List<MusicInfo> scanMusic(Context context) {
//        List<MusicInfo> musicList = new ArrayList<>();
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[]{
//                        BaseColumns._ID,
//                        MediaStore.Audio.AudioColumns.IS_MUSIC,
//                        MediaStore.Audio.AudioColumns.TITLE,
//                        MediaStore.Audio.AudioColumns.ARTIST,
//                        MediaStore.Audio.AudioColumns.ALBUM,
//                        MediaStore.Audio.AudioColumns.ALBUM_ID,
//                        MediaStore.Audio.AudioColumns.DATA,
//                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
//                        MediaStore.Audio.AudioColumns.SIZE,
//                        MediaStore.Audio.AudioColumns.DURATION
//                },
//                null,
//                null,
//                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        if (cursor == null) {
//            return musicList;
//        }
//
//        while (cursor.moveToNext()) {
//            // 是否为音乐，魅族手机上始终为0
//            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
//            if (!SystemUtils.isFlyme() && isMusic == 0) {
//                continue;
//            }
//
//            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
//            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
//            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
//            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
//            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
//            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
//            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
//            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
//
//            if(path!=null&&(path.endsWith(SUFFIX_CAPITAL)||path.endsWith(SUFFIX_LOWER))){
//                MusicInfo music = new MusicInfo();
//                music.setTitle(title);
//                music.setFilepath(path);
//                music.setFileSize(fileSize);
//                music.setAlbumId(albumId);
//                music.setFilename(fileName);
//                music.setSongId(id);
//                music.setType(MusicInfo.Type.LOCAL);
//                musicList.add(music);
//                Log.d(TAG, "id:" + id + ", title:" + title + ", artist" + artist + ", album" + album + ", albumId" + albumId
//                        + ", duration:" + duration + ", path" + path + ", fileName" + fileName + ", fileSize" + fileSize);
//            }
//        }
//        cursor.close();
//
//        return musicList;
//    }
//
//    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
//        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
//        return ContentUris.withAppendedId(artworkUri, albumId);
//    }
//
//    public static void saveMusic(Context context, MusicInfo musicInfo) {
//        ContentValues values = new ContentValues();
////        values.put(BaseColumns._ID, musicInfo.getId());
//        values.put(MediaStore.Audio.AudioColumns.TITLE, musicInfo.getTitle());
////        values.put(MediaStore.Audio.AudioColumns.ALBUM_ID, musicInfo.getAlbumId());
//        values.put(MediaStore.Audio.AudioColumns.DATA, musicInfo.getFilepath());
//        values.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, musicInfo.getFilename());
//        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
//        Uri uri = context.getContentResolver().insert(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//        MediaScannerConnection.scanFile(context,new String[] {Environment.getExternalStorageDirectory().getAbsolutePath()},
//                new String[]{"audio/x-mpeg"},
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//
//                    }
//                });
//        if(uri!=null)
//            Logger.d("uri:"+uri.toString());
//    }
}
