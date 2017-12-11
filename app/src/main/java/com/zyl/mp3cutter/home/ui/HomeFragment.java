package com.zyl.mp3cutter.home.ui;


import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.zyl.customrangeseekbar.CustomRangeSeekBar;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseFragment;
import com.zyl.mp3cutter.common.constant.CommonConstant;
import com.zyl.mp3cutter.common.ui.view.CommonDialog;
import com.zyl.mp3cutter.common.ui.view.visualizer.renderer.CircleBarRenderer;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.common.utils.SystemTools;
import com.zyl.mp3cutter.databinding.FragmentHomeBinding;
import com.zyl.mp3cutter.home.di.DaggerHomeComponent;
import com.zyl.mp3cutter.home.di.HomeModule;
import com.zyl.mp3cutter.home.presenter.HomeContract;
import com.zyl.mp3cutter.home.presenter.HomePresenter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FOLDER;

/**
 * Description: 主页 fragment类
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
@RuntimePermissions
public class HomeFragment extends BaseFragment<HomeContract.View, HomePresenter> implements HomeContract.View, View.OnClickListener {
    FragmentHomeBinding mBinding;
    // intent返回动作
    private static final int REQUEST_CODE = 0;
    // 音量面板显示和隐藏动画
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;
    // 获取系统音频对象
    private AudioManager mAudioManager;
    private ProgressDialog mProgressDialog;
    // 音乐滑块事件
    private CustomRangeSeekBar.ThumbListener mThumbListener = new CustomRangeSeekBar.ThumbListener() {

        @Override
        public void onClickMinThumb(Number max, Number min) {
        }

        @Override
        public void onClickMaxThumb() {

        }

        @Override
        public void onMinMove(Number max, Number min) {
//            mPresenter.seekTo(min.intValue());
            mPresenter.seekToForIsMin(true);
//            mBinding.rangeSeekbar.setSelectedAbsoluteMinValue(min.intValue());
        }

        @Override
        public void onMaxMove(Number max, Number min) {
//            if (max.intValue() <= min.intValue()) {
//                mPresenter.seekTo(max.intValue());
//                mBinding.rangeSeekbar.setSelectedAbsoluteMaxValue(max.intValue());
//            }
            mPresenter.seekToForIsMin(false);
        }

        @Override
        public void onUpMinThumb(Number max, Number min) {
        }

        @Override
        public void onUpMaxThumb() {
        }
    };

    /**
     * 声音滑块滑动事件
     */
    private SeekBar.OnSeekBarChangeListener mVoiceChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (seekBar.getId() == R.id.voice_seekbar) {
                // 设置音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                mBinding.visualView.invalidate();
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicPlayFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void init() {
        // 获取系统音乐音量
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        // 获取系统音乐当前音量
        int currentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mBinding.voiceSeekbar.setMax(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mBinding.voiceSeekbar.setProgress(currentVolume);

        showVoicePanelAnimation = AnimationUtils.loadAnimation(
                getActivity(), R.anim.push_up_in);
        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(
                getActivity(), R.anim.push_up_out);
    }

    /**
     * 显示剪切成功窗口
     */
    private void showCutterSuccessDialog(final String path) {
        String format = String.format(getResources().getString(R.string.homefragment_cut_success),
                CommonConstant.RING_FOLDER);
        new CommonDialog.Builder().setContext(getActivity()).setContentStr(format)
                .setOnDialogListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void doOk() {
                        if (FileUtils.bFolder(RING_FOLDER)) {
                            SystemTools.setMyRingtone(path,
                                    getActivity());
                        }
                    }
                })
                .build().show();
        //ad
//        AppConnect.getInstance(this).showPopAd(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void ComponentInject(AppComponent appComponent) {
        DaggerHomeComponent
                .builder()
                .appComponent(appComponent)
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = mBinding.getRoot();
        init();
        initListener(view);
        return view;
    }

    @Override
    public void setVisualizerViewEnaled(boolean enabled) {
        mBinding.visualView.setEnabled(enabled);
    }

    @Override
    public void checkRecordPermission(MediaPlayer mediaPlayer) {
        HomeFragmentPermissionsDispatcher.linkMediaPlayerForVisualViewWithPermissionCheck(this, mediaPlayer);
    }

    @Override
    public int getSeekbarSelectedMaxValue() {
        Number number = mBinding.rangeSeekbar.getSelectedAbsoluteMaxValue();
        return number.intValue();
    }

    @Override
    public float getSeekBarAbsoluteMaxValue() {
        return mBinding.rangeSeekbar.getAbsoluteMaxValue();
    }

    @Override
    public int getSeekbarSelectedMinValue() {
        Number number = mBinding.rangeSeekbar.getSelectedAbsoluteMinValue();
        return number.intValue();
    }

    @Override
    public void setPlayBtnStatus(boolean isPlayingStatus) {
        if (isPlayingStatus) {
            mBinding.btnPlay.setBackgroundResource(R.drawable.selector_pause_btn);
        } else {
            mBinding.btnPlay.setBackgroundResource(R.drawable.selector_play_btn);
        }
    }

    public void setSeekBarEnable(boolean isClickable) {
        mBinding.rangeSeekbar.setEnabled(isClickable);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    @Override
    public void linkMediaPlayerForVisualView(MediaPlayer player) {
        mBinding.visualView.link(player);
    }

    /**
     * 添加频谱渲染
     */
    @Override
    public void addBarGraphRenderers() {
        Paint paint2 = new Paint();
        paint2.setStrokeWidth(12f);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.argb(240, 172, 175, 64));
        // BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4,
        // paint2, false);
        CircleBarRenderer barGraphRendererTop = new CircleBarRenderer(paint2, 4);
        mBinding.visualView.clearRenderers();
        mBinding.visualView.addRenderer(barGraphRendererTop);
    }

    @Override
    public boolean setSeekBarProgressValue(int value, boolean isMin) {
        if(isMin) {
            return mBinding.rangeSeekbar.setSelectedAbsoluteMinValue(value);
        }
        else{
            return mBinding.rangeSeekbar.setSelectedAbsoluteMaxValue(value);
        }
    }

    /**
     * 恢复默认seekbar初始值
     */
    @Override
    public void resetSeekBarSelValue(){
        mBinding.rangeSeekbar.restorePercentSelectedMinValue();
        mBinding.rangeSeekbar.restorePercentSelectedMaxValue();
    }

    @Override
    public void setDuration(int value) {
        mBinding.rangeSeekbar.setAbsoluteMaxValue(value);
//        mBinding.tvEndtime.setText(TimeUtils.formatSecondTime(value));
    }

    @Override
    public void doCutterSucc(final String path) {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mProgressDialog != null && getActivity() != null
                                && !getActivity().isFinishing()) {
                            mProgressDialog.dismiss();
                            showCutterSuccessDialog(path);
                        }
                    }
                });

    }

    @Override
    public void doCutterFail() {
        Toast.makeText(getActivity(), getResources().getString(R.string.homefragment_cut_fail), Toast.LENGTH_LONG).show();
    }

    /**
     * 剪切提示弹出窗口
     */
    public void showCutterPromptDialog() {
        final Number minNumber = mBinding.rangeSeekbar.getSelectedAbsoluteMinValue();
        final Number maxNumber = mBinding.rangeSeekbar.getSelectedAbsoluteMaxValue();
        if (maxNumber.intValue() <= minNumber.intValue()) {
            Toast.makeText(getActivity(),
                    getString(R.string.dialog_cutter_warning_length),
                    Toast.LENGTH_LONG).show();
            return;
        }
//        final EditText et_fileName = new EditText(getActivity());
        new CommonDialog.Builder().setContext(getActivity()).setContentStr(getString(R.string.dialog_cutter_msg))
                .setIsShowInput(true)
                .setOnDialogListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void doOk(String text) {
                        mProgressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.homefragment_cutting_tip),
                                getResources().getString(R.string.homefragment_cutting));
                        mPresenter.doCutter(text, minNumber.intValue(),
                                maxNumber.intValue());
                    }
                })
                .build().show();
    }

    /**
     * 音乐显示和隐藏
     */
    public void voicePanelAnimation() {
        if (mBinding.rlPlayerVoice.getVisibility() == View.GONE) {
            mBinding.rlPlayerVoice.startAnimation(showVoicePanelAnimation);
            mBinding.rlPlayerVoice.setVisibility(View.VISIBLE);
        } else {
            mBinding.rlPlayerVoice.startAnimation(hiddenVoicePanelAnimation);
            mBinding.rlPlayerVoice.setVisibility(View.GONE);
        }
    }

    private void hidenVoicePanel() {
        if (mBinding.rlPlayerVoice.getVisibility() == View.VISIBLE) {
            mBinding.rlPlayerVoice.startAnimation(hiddenVoicePanelAnimation);
            mBinding.rlPlayerVoice.setVisibility(View.GONE);
        }
    }

    private void initListener(View view) {
        view.findViewById(R.id.dismiss_voicebar_space).setOnClickListener(this);
        mBinding.btnPlay.setOnClickListener(this);
        mBinding.btnCutterSure.setOnClickListener(this);
        mBinding.rangeSeekbar.setThumbListener(mThumbListener);
        mBinding.rangeSeekbar.setEnabled(false);
        mBinding.voiceSeekbar.setOnSeekBarChangeListener(mVoiceChangeListener);
        mBinding.btnSwitch.setOnClickListener(this);
    }

    /**
     * 选择文件返回
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.pause();
        setPlayBtnStatus(false);
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    void showRationaleForRecord(final PermissionRequest request) {
        new CommonDialog.Builder().setContext(getActivity()).setContentStr(
                getResources().getString(R.string.homefragment_permission_prompt))
                .setOnDialogListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void doOk() {
                        request.proceed();
                    }
                }).setIsShowOne(true).build().show();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void showRecordDenied() {
        Toast.makeText(getActivity(), getResources().getString(R.string.homefragment_permission_denied),
                Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    void onRecordNeverAskAgain() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        HomeFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
//        if (permission == Manifest.permission.RECORD_AUDIO)
//            return true;
        return super.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_play:
                mPresenter.playToggle(getActivity());
                break;
            case R.id.btn_cutter_sure:
                if (mPresenter.isSelectedMp3(getActivity()))
                    showCutterPromptDialog();
                break;
            case R.id.dismiss_voicebar_space:
                hidenVoicePanel();
                break;
            case R.id.btn_switch:
                mPresenter.switchSeekBar();
                break;
            default:
                break;
        }
    }

    public void openFile() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(new Intent(getActivity(),
                        FileChooserActivity.class), REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            } else {
                startActivityForResult(new Intent(getActivity(),
                        FileChooserActivity.class), REQUEST_CODE);
            }
        } else {
            Toast.makeText(getActivity(),
                    R.string.sdcard_unmonted_hint, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
