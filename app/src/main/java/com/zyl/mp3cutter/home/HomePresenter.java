package com.zyl.mp3cutter.home;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.mvp.BasePresenterImpl;
import com.zyl.mp3cutter.ui.FileChooserActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class HomePresenter extends BasePresenterImpl<HomeContract.View> implements HomeContract.Presenter{
    private MediaPlayer mMediaPlayer;
    private String mSelMusicPath;
    private static final int REQUEST_CODE = 0;

    public HomePresenter() {
        mMediaPlayer = new MediaPlayer();
    }

    private Disposable mDisposable;
    private Observable<Long> mUpdateProgressObservable = Observable.interval(0, 1, TimeUnit.SECONDS);
    private Consumer mUpdateProgressConsumer = new Consumer() {
        @Override
        public void accept(Object o) throws Exception {
            mView.refreshSeekBarForValue(getCurPosition());
            Number maxValue = mView.getSeekbarMaxValue();
            // 播放完暂停处理
            int curPosition = getCurPosition();
            if (curPosition >= maxValue.intValue()) {
                pause();
                mView.setPlayBtnStatus(false);
//                mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
            }
            // 消息处理
            if (getCurPosition() >= maxValue
                    .intValue()) {
                if(mDisposable!=null) {
                    mDisposable.dispose();
                    mDisposable = null;
                }
//                mPlayerProgressHandler.removeMessages(UPDATE_PLAY_PROGRESS);
            }
//            else {
//                mDisposable = mUpdateProgressObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(mUpdateProgressConsumer);
////                mPlayerProgressHandler
////                        .sendEmptyMessage(UPDATE_PLAY_PROGRESS);
//            }
        }
    };

    @Override
    public void playToggle(Activity activity) {
        if (isPlaying()) {
            // 暂停
            pause();
            if(mDisposable!=null) {
                mDisposable.dispose();
                mDisposable = null;
            }
//            mPlayerProgressHandler.removeMessages(UPDATE_PLAY_PROGRESS);
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,new String[]{
                        android.Manifest.permission.RECORD_AUDIO},1);
            }
            else {
                mView.setVisualizerViewEnaled(false);
            }
            mView.setPlayBtnStatus(false);
        } else {
            // 播放
            if (TextUtils.isEmpty(mSelMusicPath)) {
                Toast.makeText(activity,
                        activity.getString(R.string.dialog_cutter_warning_sel),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mView.setPlayBtnStatus(true);
            seekTo(mView.getSeekbarCurValue());
            play();
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{
                        android.Manifest.permission.RECORD_AUDIO},1);
            }
            else {
                mView.setVisualizerViewEnaled(true);
            }
//            Message message = new Message();
//            message.what = UPDATE_PLAY_PROGRESS;
//            mPlayerProgressHandler.sendMessage(message);
            mDisposable = mUpdateProgressObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(mUpdateProgressConsumer);
        }

    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void seekTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    @Override
    public void play() {
        mMediaPlayer.start();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void setDataSource(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare() {
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getCurPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 选择文件返回
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        // 文件选择返回结果
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            mSelMusicPath = data
                    .getStringExtra(FileChooserActivity.EXTRA_FILE_CHOOSER);
            try {
                if (!TextUtils.isEmpty(mSelMusicPath)) {
                    mView.setSeekbarValue(0, 0);
//                    mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
                    mView.setPlayBtnStatus(false);

                    pause();
                    reset();
                    setDataSource(mSelMusicPath);
                    prepare();
                    mView.setSeekbarMax(getDuration());
                    if (ContextCompat.checkSelfPermission(mView.getContext(), android.Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mView.getContext(), new String[]{
                                android.Manifest.permission.RECORD_AUDIO}, 1);
                    } else {
                        mView.linkMediaPlayerForVisualView(getMediaPlayer());
                    }
                    mView.addBarGraphRenderers();
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if(mDisposable!=null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}
