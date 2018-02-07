package com.zyl.mp3cutter.home.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.widget.Toast;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.home.bean.MusicInfo;
import com.zyl.mp3cutter.home.ui.FileChooserActivity;
import com.zyl.mp3cutter.mp3cut.logic.Mp3CutLogic;
import com.zyl.mp3cutter.mp3cut.logic.Mp3InfoUtils;
import com.zyl.mp3cutter.mp3cut.util.Mp3NameConvertUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FOLDER;
import static com.zyl.mp3cutter.home.ui.HomeFragment.REQUEST_CODE;

/**
 * Description: Home presenter
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {
    public MediaPlayer mMediaPlayer;
    private String mSelMp3Path = "";
    private MusicInfo mSelMp3Info;
    // 标识当前播放的滑块为min or max
    private boolean mIsMinFlag = true;
    // 获取系统音频对象
    private AudioManager mAudioManager;

    @Inject
    public HomePresenter(HomeContract.View view) {
        super(view);
        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) mView.getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private Disposable mDisposable;
    private Observable<Long> mUpdateProgressObservable = Observable.interval(0, 1, TimeUnit.SECONDS);
    private Consumer mUpdateProgressConsumer = new Consumer() {
        @Override
        public void accept(Object o) throws Exception {
            if (mView == null)
                return;
            int curPosition = getCurPosition();
            Number maxValue = mView.getSeekBarAbsoluteMaxValue();

            if (!mView.setSeekBarProgressValue(curPosition, mIsMinFlag) || curPosition >= maxValue
                    .intValue()) {
                pause();
            }
        }
    };

    /**
     * 取消更新seekbar rx轮询事件
     */
    private void cancelUpdateProgress() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    /**
     * 剪切音乐
     */
    @Override
    public void doCutter(final String mp3Title, final long minValue, final long maxValue) {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                Mp3CutLogic helper = new Mp3CutLogic(new File(mSelMp3Path));
                if (FileUtils.bFolder(RING_FOLDER)) {
                    if (!TextUtils.isEmpty(mp3Title)) {
                        String targetMp3FilePath = RING_FOLDER + "/" + mp3Title +".mp3";

                        try {
                            helper.generateNewMp3ByTime(targetMp3FilePath, minValue, maxValue);
                            addMp3ToDb(mp3Title, targetMp3FilePath);
                            e.onNext(targetMp3FilePath);
                        } catch (Exception e1) {
                            e.onError(e1);
                        }
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {
                        String cutterPath = value;
                        if (mView != null) {
                            mView.doCutterSucc(cutterPath);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.doCutterFail();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 添加mp3到数据库
     *
     * @param cutterPath
     */
    private void addMp3ToDb(String title, String cutterPath) throws CloneNotSupportedException {
        File file = new File(cutterPath);
        MusicInfo info = new MusicInfo();
        info.setStorageType(MusicInfo.Type.LOCAL);
        info.setFilepath(cutterPath);
        info.setFilename(Mp3NameConvertUtils.titleConvertName(title));
        info.setTitle(title);
        info.setCoverPath(Mp3InfoUtils.getCoverPath(cutterPath, title));
        try {
            info.setFileSize(FileUtils.getFileSizes(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyApplication.getInstances().
                getBoxStore().boxFor(MusicInfo.class).put(info);
//        Mp3Utils.saveMusic(MyApplication.instances, info);
    }

    @Override
    public void playToggle(Activity activity) {
        if (isPlaying()) {
            // 暂停
            pause();
        } else {
            play(activity);
        }

    }

    @Override
    public void pause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
            mView.setPlayBtnWithStatus(false);
            mView.setVisualizerViewEnaled(false);
            cancelUpdateProgress();
        }
    }

    private void seekTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    @Override
    public void play(Activity activity) {
        // 播放
        if (TextUtils.isEmpty(mSelMp3Path)) {
            Toast.makeText(activity,
                    activity.getString(R.string.dialog_cutter_warning_sel),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaPlayer.start();
        mView.setPlayBtnWithStatus(true);
        mView.setVisualizerViewEnaled(true);
        seekToForIsMin();
        //开启进度rx轮询事件
        mDisposable = mUpdateProgressObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribe(mUpdateProgressConsumer);
    }

    private void reset() {
        mMediaPlayer.reset();
    }

    private void setDataSource(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepare() {
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private int getDuration() {
        return mMediaPlayer.getDuration();
    }

    private boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    private int getCurPosition() {
        return mMediaPlayer.getCurrentPosition();
    }


    /**
     * 判断当前是否已选择mp3
     */
    @Override
    public boolean isSelectedMp3(Context context) {
        if (TextUtils.isEmpty(mSelMp3Path)) {
            Toast.makeText(context,
                    context.getString(R.string.dialog_cutter_warning_sel),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void switchSeekBar() {
        mIsMinFlag = !mIsMinFlag;
        seekToForIsMin();
    }

    private void seekToForIsMin() {
        int curValue;
        if (mIsMinFlag) {
            curValue = mView.getSeekbarSelectedMinValue();
        } else {
            curValue = mView.getSeekbarSelectedMaxValue();
        }
        seekTo(curValue);
    }

    @Override
    public void seekToForIsMin(boolean isMinBar) {
        if (judgeIsPlayingThumb(isMinBar)) {
            int curValue;
            if (isMinBar) {
                curValue = mView.getSeekbarSelectedMinValue();
            } else {
                curValue = mView.getSeekbarSelectedMaxValue();
            }
            seekTo(curValue);
        }
    }

    /**
     * //判断滑动的滑块是否为当前正在播放的滑块
     *
     * @param isMinBar 滑动的滑块 true: min  false: max
     */
    private boolean judgeIsPlayingThumb(boolean isMinBar) {
        boolean isPlaying = false;
        if (mIsMinFlag) {
            if (isMinBar) {
                isPlaying = true;
            }
        } else {
            if (!isMinBar) {
                isPlaying = true;
            }
        }
        return isPlaying;
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
            mSelMp3Path = data
                    .getStringExtra(FileChooserActivity.EXTRA_FILEPATH_CHOOSER);
            mSelMp3Info = data
                    .getParcelableExtra(FileChooserActivity.EXTRA_FILE_CHOOSER);
            try {
                if (!TextUtils.isEmpty(mSelMp3Path)) {
                    mView.resetSeekBarSelValue();
                    mView.setPlayBtnWithStatus(false);
                    mView.setSeekBarEnable(true);
                    pause();
                    reset();
                    setDataSource(mSelMp3Path);
                    prepare();
                    mView.setSeekBarMaxValue(getDuration());
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

    @Override
    public void setStreamVolume(int progress) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                progress, 0);
    }

    @Override
    public int getStreamMaxVolume() {
        return mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int getStreamVolume() {
        return mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
    }
}
