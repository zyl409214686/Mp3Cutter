package com.zyl.mp3cutter.home.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.zyl.mp3cutter.common.base.IBasePresenter;
import com.zyl.mp3cutter.common.base.IBaseView;

/**
 * Description: mvp contract契约类
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */

public class HomeContract {
    public interface View extends IBaseView {
        void setVisualizerViewEnaled(boolean enabled);

        void checkRecordPermission(MediaPlayer mediaPlayer);

        int getSeekbarCurValue();

        int getSeekbarMaxValue();

        int getSeekbarMinValue();

        void setPlayBtnStatus(boolean isPlayingStatus);

        void setSeekbarValue(int selmin, int selcur);

        void linkMediaPlayerForVisualView(MediaPlayer player);

        void addBarGraphRenderers();

        /**
         * 设置当前播放值
         * @param value
         */
        void setPlayCurValue(int value);

        /**
         * 设置时长
         */
        void setDuration(int value);

        /**
         * 剪切成功
         */
        void doCutterSucc(String path);
    }

    interface Presenter extends IBasePresenter {
        void playToggle(Activity activity);

        void pause();

        void seekTo(int progress);

        void play();

        void reset();

        void setDataSource(String path);

        void prepare();

        void onSpeedDown();

        void onTouchSpeedFastUp();

        void onBackword();

        MediaPlayer getMediaPlayer();

        int getDuration();

        boolean isPlaying();

        int getCurPosition();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onDestroy();

        /**
         * 剪切音乐
         * @param fileName
         * @param minValue
         * @param maxValue
         */
        void doCutter(final String fileName, final int minValue, final int maxValue);

        /**
         * 判断是否已选择mp3
         * @param context
         * @return
         */
        boolean isSelectedMp3(Context context);
    }
}
