package com.zyl.mp3cutter.home;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;

import com.zyl.mp3cutter.common.mvp.BasePresenter;
import com.zyl.mp3cutter.common.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomeContract {
    interface View extends BaseView {
        void setVisualizerViewEnaled(boolean enabled);

        int getSeekbarCurValue();

        int getSeekbarMaxValue();

        int getSeekbarMinValue();

        void setPlayBtnStatus(boolean isPlayingStatus);

        void refreshSeekBarForValue(int value);

        void setSeekbarValue(int selmin, int selcur);

        void setSeekbarMax(int max);

        void linkMediaPlayerForVisualView(MediaPlayer player);

        void addBarGraphRenderers();
    }

    interface Presenter extends BasePresenter<View> {
        void playToggle(Activity activity);

        void pause();

        void seekTo(int progress);

        void play();

        void reset();

        void setDataSource(String path);

        void prepare();

        MediaPlayer getMediaPlayer();

        int getDuration();

        boolean isPlaying();

        int getCurPosition();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onDestroy();
    }
}
