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
        /**
         *  启动或关闭频谱
         * @param enabled
         */
        void setVisualizerViewEnaled(boolean enabled);

        /**
         * 检测录音权限， 频谱
         * @param mediaPlayer
         */
        void checkRecordPermission(MediaPlayer mediaPlayer);

        /**
         * 获取seekbar最大值
         * @return
         */
        float getSeekBarAbsoluteMaxValue();

        /**
         * 获取seekbar min thumb滑块对应的值
         * @return
         */
        int getSeekbarSelectedMinValue();

        /**
         * 获取seekbar max thumb滑块对应的值
         * @return
         */
        int getSeekbarSelectedMaxValue();

        /**
         * 根据播放状态设置播放按钮背景
         * @param isPlayingStatus
         */
        void setPlayBtnWithStatus(boolean isPlayingStatus);

        /**
         * 添加频谱渲染
         */
        void addBarGraphRenderers();

        /**
         * seekbar 设置seekbar 滑块是否可用（可以滑动）
         * @param isEnable
         */
        void setSeekBarEnable(boolean isEnable);

        /**
         * 设置当前播放值
         *
         * @param value
         */
        boolean setSeekBarProgressValue(int value, boolean isMin);

        /**
         * 设置seekbar最大值
         */
        void setSeekBarMaxValue(int value);

        /**
         * 剪切成功
         */
        void doCutterSucc(String path);

        /**
         * 剪切失败
         */
        void doCutterFail();

        /**
         * 设置seekbar选择滑块恢复默认
         */
        void resetSeekBarSelValue();
    }


    interface Presenter extends IBasePresenter {
        /**
         * 播放暂停切换
         * @param activity
         */
        void playToggle(Activity activity);

        /**
         * 暂停
         */
        void pause();

        /**
         * 销毁
         */
        void onDestroy();

        /**
         * activty回调
         * @param requestCode
         * @param resultCode
         * @param data
         */
        void onActivityResult(int requestCode, int resultCode, Intent data);

        /**
         * 设置系统声音值
         * @param progress
         */
        void setStreamVolume(int progress);

        /**
         * 获取系统最大声音值
         */
        int getStreamMaxVolume();

        /**
         * 获取系统最当前声音值
         */
        int getStreamVolume();

        /**
         * 剪切音乐
         *
         * @param fileName
         * @param minValue
         * @param maxValue
         */
        void doCutter(final String fileName, final long minValue, final long maxValue);

        /**
         * 判断是否已选择mp3
         *
         * @param context
         * @return
         */
        boolean isSelectedMp3(Context context);

        /**
         * 切换当前播放的滑块min or  max
         */
        void switchSeekBar();

        /**
         * mediaplayer 根据传入参数 flag 跳到指定滑块（min or max）对应的位置。
         */
        void seekToForIsMin(boolean isMinBar);
    }
}
