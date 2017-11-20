package com.zyl.mp3cutter.home.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseFragment;
import com.zyl.mp3cutter.common.ui.view.CommonDialog;
import com.zyl.mp3cutter.common.ui.view.RangeSeekBar;
import com.zyl.mp3cutter.common.ui.view.visualizer.VisualizerView;
import com.zyl.mp3cutter.common.ui.view.visualizer.renderer.CircleBarRenderer;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.common.utils.SystemTools;
import com.zyl.mp3cutter.common.utils.TimeUtils;
import com.zyl.mp3cutter.databinding.FragmentHomeBinding;
import com.zyl.mp3cutter.home.di.DaggerHomeComponent;
import com.zyl.mp3cutter.home.di.HomeModule;
import com.zyl.mp3cutter.home.presenter.HomeContract;
import com.zyl.mp3cutter.home.presenter.HomePresenter;

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
public class HomeFragment extends BaseFragment<HomeContract.View, HomePresenter> implements HomeContract.View {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    private ImageButton mPlayBtn, mCutterBtn, mSpeedBtn, mBackwardBtn;
    private TextView mPlayerStartTimeTV, mPlayerEndTimeTV;
    private VisualizerView mVisualView;
    private RangeSeekBar<Integer> mPlaySeekBar;
    private TextView mVoiceBtn, mChooseBtn;
    // intent返回动作
    private static final int REQUEST_CODE = 0;
    private boolean mIsTouching;
    private RelativeLayout rl_player_voice;
    // 音量面板显示和隐藏动画
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;
    // 调节音量
    private SeekBar mVoiceSeekBar;
    // 获取系统音频对象
    private AudioManager mAudioManager;

    /**
     * 声音滑块滑动事件
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (seekBar.getId() == R.id.sb_player_voice) {
                // 设置音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                mVisualView.invalidate();
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicPlayFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initView(View view) {
        mChooseBtn = (TextView) view.findViewById(R.id.btn_player_open);//.setOnClickListener(this);
        mPlayBtn = (ImageButton) view.findViewById(R.id.btn_play);
        mPlaySeekBar = (RangeSeekBar) view.findViewById(R.id.sb_player_cutterprogress);
        mCutterBtn = (ImageButton) view.findViewById(R.id.btn_cutter_sure);
        mSpeedBtn = (ImageButton) view.findViewById(R.id.btn_speed);
        mBackwardBtn = (ImageButton) view.findViewById(R.id.btn_backward);
        mPlayerStartTimeTV = (TextView) view.findViewById(R.id.tv_player_playing_time);
        mPlayerEndTimeTV = (TextView) view.findViewById(R.id.tv_player_playering_end);
        rl_player_voice = (RelativeLayout) view.findViewById(R.id.rl_player_voice);
        mVoiceBtn = (TextView) view.findViewById(R.id.btn_player_voice);
        mVoiceSeekBar = (SeekBar) view.findViewById(R.id.sb_player_voice);
        // 频谱视图
        mVisualView = (VisualizerView) view.findViewById(R.id.visual_view);
    }


    private void init() {
//        myMediaPlayer = new MediaPlayer();
        mPlaySeekBar.setSelectedMinValue(0);
        mPlaySeekBar.setSelectedMaxValue(100);
        mPlaySeekBar.setSelectedCurValue(0);
        // 获取系统音乐音量
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        // 获取系统音乐当前音量
        int currentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mVoiceSeekBar.setMax(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mVoiceSeekBar.setProgress(currentVolume);

        showVoicePanelAnimation = AnimationUtils.loadAnimation(
                getActivity(), R.anim.push_up_in);
        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(
                getActivity(), R.anim.push_up_out);
    }

    /**
     * 显示剪切成功窗口
     */
    private void showCutterSuccessDialog(final String path) {
        new CommonDialog.Builder().setContext(getActivity()).setContentStr(getString(R.string.dialog_cutter_success))
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
                .homeModule(new HomeModule(this)) //请将TempLateModule()第一个首字母改为小写
                .build()
                .inject(this);

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        // Inflate the layout for this fragment
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        initView(view);
        init();
        initListener();
        return view;
    }

    @Override
    public void setVisualizerViewEnaled(boolean enabled) {
        mVisualView.setEnabled(enabled);
    }

    @Override
    public void checkRecordPermission(MediaPlayer mediaPlayer){
        HomeFragmentPermissionsDispatcher.linkMediaPlayerForVisualViewWithPermissionCheck(this, mediaPlayer);
    }

    @Override
    public int getSeekbarCurValue() {
        Number number = mPlaySeekBar.getSelectedCurValue();
        return number.intValue();
    }

    @Override
    public int getSeekbarMaxValue() {
        Number number = mPlaySeekBar.getSelectedMaxValue();
        return number.intValue();
    }

    @Override
    public int getSeekbarMinValue() {
        Number number = mPlaySeekBar.getSelectedMinValue();
        return number.intValue();
    }

    @Override
    public void setPlayBtnStatus(boolean isPlayingStatus) {
        if (isPlayingStatus) {
            mPlayBtn.setBackgroundResource(R.drawable.selector_pause_btn);
        } else {
            mPlayBtn.setBackgroundResource(R.drawable.selector_play_btn);
        }
    }

    @Override
    public void setSeekbarValue(int selmin, int selcur) {
        mPlaySeekBar.setSelectedMinValue(selmin);
        mPlaySeekBar.setSelectedCurValue(selcur);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    @Override
    public void linkMediaPlayerForVisualView(MediaPlayer player) {
        mVisualView.link(player);
    }

    /**
     * 添加柱状频谱渲染
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
        mVisualView.clearRenderers();
        mVisualView.addRenderer(barGraphRendererTop);
    }

    @Override
    public void setPlayCurValue(int value) {
        mPlayerStartTimeTV.setText(TimeUtils.formatSecondTime(value));
    }

    @Override
    public void setDuration(int value) {
        mPlaySeekBar.setAbsoluteMaxValue(value);
        mPlayerEndTimeTV.setText(TimeUtils.formatSecondTime(value));
    }

    @Override
    public void doCutterSucc(String path) {
        showCutterSuccessDialog(path);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    /**
     * 剪切提示弹出窗口
     */
    private void showCutterPromptDialog() {
        final Number minNumber = mPlaySeekBar.getSelectedMinValue();
        final Number maxNumber = mPlaySeekBar.getSelectedMaxValue();
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
                        mPresenter.doCutter(text, minNumber.intValue(),
                                maxNumber.intValue());
                    }
                })
                .build().show();
    }

    /**
     * 音乐显示和隐藏
     */
    private void voicePanelAnimation() {
        if (rl_player_voice.getVisibility() == View.GONE) {
            rl_player_voice.startAnimation(showVoicePanelAnimation);
            rl_player_voice.setVisibility(View.VISIBLE);
        } else {
            rl_player_voice.startAnimation(hiddenVoicePanelAnimation);
            rl_player_voice.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        /**
         * 选择音乐
         */
        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED))
                    startActivityForResult(new Intent(getActivity(),
                            FileChooserActivity.class), REQUEST_CODE);
                else
                    Toast.makeText(getActivity(),
                            R.string.sdcard_unmonted_hint, Toast.LENGTH_SHORT)
                            .show();
            }
        });


        /**
         * 播放音乐
         */

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.playToggle(getActivity());
            }
        });

        /**
         * 剪切音乐
         */
        mCutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter.isSelectedMp3(getActivity()))
                    showCutterPromptDialog();
            }
        });

        /**
         * 快进功能
         */
        mSpeedBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new Thread() {
                            @Override
                            public void run() {
                                mIsTouching = true;
                                while (mIsTouching) {
                                    Number curNumber = mPlaySeekBar
                                            .getSelectedMaxValue();
                                    if (mPresenter.getCurPosition() + 500 < curNumber
                                            .doubleValue())
                                        mPresenter.seekTo(mPresenter
                                                .getCurPosition() + 500);
                                    else if (mPresenter.getCurPosition() + 500 == curNumber
                                            .doubleValue()) {
                                        mPresenter.seekTo(mPresenter
                                                .getDuration());
                                        mIsTouching = false;
                                    } else {
                                        mIsTouching = false;
                                    }
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                super.run();
                            }
                        }.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        mIsTouching = false;
                        break;
                }
                return false;
            }
        });

        /**
         * 快退功能
         */
        mBackwardBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new Thread() {
                            @Override
                            public void run() {
                                mIsTouching = true;
                                while (mIsTouching) {
                                    Number minNumber = mPlaySeekBar
                                            .getSelectedMinValue();
                                    if (mPresenter.getCurPosition() - 500 > minNumber
                                            .doubleValue())
                                        mPresenter.seekTo(mPresenter
                                                .getCurPosition() - 500);
                                    else if (mPresenter.getCurPosition() - 500 == minNumber
                                            .doubleValue()) {
                                        mPresenter.seekTo(mPresenter
                                                .getDuration());
                                        mIsTouching = false;
                                    } else {
                                        mIsTouching = false;
                                    }
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                super.run();
                            }
                        }.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        mIsTouching = false;
                        break;
                }
                return false;
            }
        });

        /**
         * 音量控制
         */
        mVoiceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                voicePanelAnimation();
            }
        });

        /**
         * 音量滑块滑动事件
         */
        mVoiceSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        /**
         * 音乐滑块点击事件
         */
        mPlaySeekBar.setThumbListener(new RangeSeekBar.ThumbListener() {

            @Override
            public void onClickMinThumb(Number max, Number min, Number cur) {
                if (min.intValue() >= cur.intValue()) {
                    mPresenter.pause();
                    setPlayBtnStatus(false);
                }
            }

            @Override
            public void onClickMaxThumb() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMinMove(Number max, Number min, Number cur) {
                // TODO Auto-generated method stub
                if (min.intValue() > cur.intValue()) {
                    mPresenter.seekTo(min.intValue());
                    mPlaySeekBar.setSelectedCurValue(min.intValue());
                }
            }

            @Override
            public void onMaxMove(Number max, Number min, Number cur) {
                // TODO Auto-generated method stub
                if (max.intValue() < cur.intValue()) {
                    mPresenter.seekTo(max.intValue());
                    mPlaySeekBar.setSelectedCurValue(max.intValue());
                }
            }

            @Override
            public void onUpMinThumb(Number max, Number min, Number cur) {
                if (min.intValue() >= cur.intValue()) {
                    mPresenter.play();
                    setPlayBtnStatus(true);
                }
            }

            @Override
            public void onUpMaxThumb() {
                // TODO Auto-generated method stub

            }

        });

        /**
         * 滑块滑动事件
         */
        mPlaySeekBar
                .setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

                    @Override
                    public void rangeSeekBarValuesChanged(
                            RangeSeekBar<Integer> rangeSeekBar,
                            Number minValue, Number maxValue) {
                        mPlaySeekBar.invalidate();
                    }

                });
    }


    /**
     * 选择文件返回
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ⑨重写onRequestPermissionsResult方法
     * 获取动态权限请求的结果,再开启录制音频
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//        } else {
//            Toast.makeText(getActivity(), "用户拒绝了权限", Toast.LENGTH_SHORT).show();
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
    @Override
    public void onStop() {
        super.onStop();
        mPresenter.pause();
        setPlayBtnStatus(false);
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    void showRationaleForRecord(final PermissionRequest request) {
        new CommonDialog.Builder().setContext(getActivity()).setContentStr("亲爱的用户，显示声音频谱需要录音权限，请您知晓~")
                .setOnDialogListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void doOk() {
                        request.proceed();
                    }
                }).setIsShowOne(true).build().show();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void showRecordDenied() {
        Toast.makeText(getActivity(), "已拒绝录音权限，将不会显示音乐频谱。如需要显示请到系统权限管理设置。", Toast.LENGTH_LONG).show();
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
        if(permission==Manifest.permission.RECORD_AUDIO)
            return true;
        return super.shouldShowRequestPermissionRationale(permission);
    }
}
