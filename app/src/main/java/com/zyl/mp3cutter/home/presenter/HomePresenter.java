package com.zyl.mp3cutter.home.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.widget.Toast;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.home.ui.FileChooserActivity;
import com.zyl.mp3cutter.mp3separate.bean.Mp3Fenge;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FOLDER;
import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FORMAT;

/**
 * Description: degger2 module类
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {
    public MediaPlayer mMediaPlayer;
    private String mSelMusicPath = "";
    private static final int REQUEST_CODE = 0;
    private boolean mIsTouching;

    @Inject
    public HomePresenter(HomeContract.View view) {
        super(view);
        mMediaPlayer = new MediaPlayer();
    }

    private Disposable mDisposable;
    private Observable<Long> mUpdateProgressObservable = Observable.interval(0, 1, TimeUnit.SECONDS);
    private Consumer mUpdateProgressConsumer = new Consumer() {
        @Override
        public void accept(Object o) throws Exception {
            if (mView == null)
                return;
            mView.setPlayCurValue(getCurPosition());
            Number maxValue = mView.getSeekbarMaxValue();
            // 播放完暂停处理
            int curPosition = getCurPosition();
            if (curPosition >= maxValue.intValue()) {
                pause();
                mView.setPlayBtnStatus(false);
            }
            // 消息处理
            if (getCurPosition() >= maxValue
                    .intValue()) {
                if (mDisposable != null) {
                    mDisposable.dispose();
                    mDisposable = null;
                }
            }
        }
    };

    /**
     * 剪切音乐
     */
    @Override
    public void doCutter(final String fileName, final int minValue, final int maxValue) {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                Mp3Fenge helper = new Mp3Fenge(new File(mSelMusicPath));
                if (FileUtils.bFolder(RING_FOLDER)) {
                    if (!TextUtils.isEmpty(fileName)) {
                        String cutterPath = RING_FOLDER + "/" + fileName + RING_FORMAT;
                        if (helper.generateNewMp3ByTime(new File(cutterPath), minValue, maxValue)) {
                            e.onNext(cutterPath);
                        }
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        String cutterPath = (String) o;
                        if (mView != null) {
                            mView.doCutterSucc(cutterPath);
                        }
                    }
                });
    }

    @Override
    public void playToggle(Activity activity) {
        if (isPlaying()) {
            // 暂停
            pause();
            if (mDisposable != null) {
                mDisposable.dispose();
                mDisposable = null;
            }
            mView.setVisualizerViewEnaled(false);
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
            mView.setVisualizerViewEnaled(true);
            mDisposable = mUpdateProgressObservable.observeOn(AndroidSchedulers.mainThread()).
                    subscribe(mUpdateProgressConsumer);
        }

    }

    /**
     * 快进
     */
    @Override
    public void onSpeedDown() {
        new Thread() {
            @Override
            public void run() {
                mIsTouching = true;
                while (mIsTouching) {
                    Number curNumber = mView
                            .getSeekbarMaxValue();
                    if (getCurPosition() + 500 < curNumber
                            .doubleValue())
                        seekTo(getCurPosition() + 500);
                    else if (getCurPosition() + 500 == curNumber
                            .doubleValue()) {
                        seekTo(getDuration());
                        mIsTouching = false;
                    } else {
                        mIsTouching = false;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        }.start();
    }

    @Override
    public void onTouchSpeedFastUp() {
        mIsTouching = false;
    }

    @Override
    public void onBackword() {
        new Thread() {
            @Override
            public void run() {
                mIsTouching = true;
                while (mIsTouching) {
                    if(mView==null)
                        return;
                    Number minNumber = mView
                            .getSeekbarMinValue();
                    if (getCurPosition() - 500 > minNumber
                            .doubleValue())
                        seekTo(getCurPosition() - 500);
                    else if (getCurPosition() - 500 == minNumber
                            .doubleValue()) {
                        seekTo(getDuration());
                        mIsTouching = false;
                    } else {
                        mIsTouching = false;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        }.start();
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
     * 判断当前是否已选择mp3
     */
    @Override
    public boolean isSelectedMp3(Context context) {
        if (TextUtils.isEmpty(mSelMusicPath)) {
            Toast.makeText(context,
                    context.getString(R.string.dialog_cutter_warning_sel),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                    mView.setPlayBtnStatus(false);
                    pause();
                    reset();
                    setDataSource(mSelMusicPath);
                    prepare();
                    mView.setDuration(getDuration());
                    mView.checkRecordPermission(getMediaPlayer());
                    mView.addBarGraphRenderers();
                }
            } catch (IllegalArgumentException e) {
                // Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}
