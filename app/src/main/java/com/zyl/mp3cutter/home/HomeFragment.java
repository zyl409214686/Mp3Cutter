package com.zyl.mp3cutter.home;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.mvp.MVPBaseFragment;
import com.zyl.mp3cutter.common.ui.view.RangeSeekBar;
import com.zyl.mp3cutter.common.ui.view.XfDialog;
import com.zyl.mp3cutter.common.ui.view.visualizer.VisualizerView;
import com.zyl.mp3cutter.common.ui.view.visualizer.renderer.CircleBarRenderer;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.common.utils.SystemTools;
import com.zyl.mp3cutter.common.utils.TimeUtils;
import com.zyl.mp3cutter.common.utils.ViewUtils;
import com.zyl.mp3cutter.mp3fenge.bean.Mp3Fenge;
import com.zyl.mp3cutter.ui.FileChooserActivity;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomeFragment extends MVPBaseFragment<HomeContract.View, HomePresenter> implements HomeContract.View {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    private ImageButton mPlayBtn, mCutterBtn, mSpeedBtn, mBackwardBtn;

    private TextView mPlayerTimeTV, mPlayerDurationTV;
    private VisualizerView mVisualView;
    private RelativeLayout mRlMain;
    private RangeSeekBar<Integer> mPlaySeekBar;
    //    private MediaPlayer myMediaPlayer;
    private TextView mVoiceBtn, mChooseBtn;
    // intent返回动作
    private static final int REQUEST_CODE = 0;
    // handler 处理
    private int UPDATE_PLAY_PROGRESS = 0;
    private int CUTTER_SUCCESS = 1;

    // 播放状态
    private int STATUS_PLAYING = 2;
    private int STATUS_PAUSE = 3;
    private String mSelMusicPath;
    // 频谱对象
    // private Visualizer mVisualizer;
    private boolean mIsTouching;
    private RelativeLayout rl_player_voice;
    // 音量面板显示和隐藏动画
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;
    // 调节音量
    private SeekBar mVoiceSeekBar;
    // 获取系统音频对象
    private AudioManager mAudioManager;
    // 铃声地址
    private static String RING_FOLDER = "/sdcard/MUSIC_CUTTER";
    // 铃声格式
    private static final String RING_FORMAT = ".mp3";
    // 铃声路径
    private static final String RANG_PATH = "RANG_PATH";

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


    private Handler mChangStatusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == STATUS_PLAYING) {
                mPlayBtn.setBackgroundResource(R.drawable.selector_pause_btn);
            } else if (msg.what == STATUS_PAUSE) {
                mPlayBtn.setBackgroundResource(R.drawable.selector_play_btn);
            }
        }
    };

    private Handler mPlayerProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_PLAY_PROGRESS) {
                mPlaySeekBar.setSelectedCurValue(mPresenter.getCurPosition());
                mPlayerTimeTV.setText(TimeUtils.formatSecondTime(mPresenter.getCurPosition()));
                mPlayerDurationTV.setText(TimeUtils.formatSecondTime(mPresenter.getCurPosition()));
                Number maxValue = mPlaySeekBar.getSelectedMaxValue();
                // 播放完暂停处理
                if (mPresenter.getCurPosition() >= maxValue.intValue()) {
                    mPresenter.pause();
                    mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
                }
                // 消息处理
                if (mPresenter.getCurPosition() >= mPresenter.getCurPosition()
                        || mPresenter.getCurPosition() >= maxValue
                        .intValue())
                    mPlayerProgressHandler.removeMessages(UPDATE_PLAY_PROGRESS);
                else
                    mPlayerProgressHandler
                            .sendEmptyMessage(UPDATE_PLAY_PROGRESS);
            } else if (msg.what == CUTTER_SUCCESS) {
                Bundle b = msg.getData();
                String path = b.getString(RANG_PATH);
                showCutterSuccessDialog(path);
            } else if (msg.what == 100) {
                Bundle b = msg.getData();
                String albumPic = b.getString("setPIC") + ".png";
                Bitmap bitmap = BitmapFactory.decodeFile(albumPic);
                BitmapDrawable bmpDraw = new BitmapDrawable(bitmap);
                mRlMain.setBackgroundDrawable(bmpDraw);
            }
            super.handleMessage(msg);
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
    // TODO: Rename and change types and number of parameters
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        init();
        initListener();
        return view;
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
        mPlayerTimeTV = (TextView) view.findViewById(R.id.tv_player_playing_time);
        mPlayerDurationTV = (TextView) view.findViewById(R.id.tv_player_playering_duration);
        rl_player_voice = (RelativeLayout) view.findViewById(R.id.rl_player_voice);
        mVoiceBtn = (TextView) view.findViewById(R.id.btn_player_voice);
        mVoiceSeekBar = (SeekBar) view.findViewById(R.id.sb_player_voice);
        // mResetBtn = (ImageButton) findViewById(R.id.btn_reset);
        // 频谱视图
        mVisualView = (VisualizerView) view.findViewById(R.id.visual_view);
        mRlMain = (RelativeLayout) view.findViewById(R.id.rl_main);
        Observable.interval(0,5, TimeUnit.SECONDS).subscribe(new Observer<Long>() {

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d("HomeFragment", "------>along："+aLong+" time:"+ SystemClock.elapsedRealtime());
            }
        });
    }


    private void init() {
//        myMediaPlayer = new MediaPlayer();
        mPlaySeekBar.setSelectedMinValue(0);
        mPlaySeekBar.setSelectedMaxValue(100);
        mPlaySeekBar.setSelectedCurValue(0);
        mPlaySeekBar.setEnabled(false);
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
     * 判断当前是否有音乐
     */
    private boolean isSelMusice() {
        if (TextUtils.isEmpty(mSelMusicPath)) {
            Toast.makeText(getActivity(),
                    getString(R.string.dialog_cutter_warning_sel),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * 显示剪切成功窗口
     */
    public void showCutterSuccessDialog(final String path) {
        new XfDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_cutter_success))
                .setPositiveButton(getString(R.string.dialog_btn_sure),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (FileUtils.bFolder(RING_FOLDER)) {
                                    SystemTools.setMyRingtone(path,
                                            getActivity());
                                }
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_btn_cancel), null)
                .create().show();
        //ad
//        AppConnect.getInstance(this).showPopAd(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setVisualizerViewEnaled(boolean enabled) {
        mVisualView.setEnabled(false);
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
    public void refreshSeekBarForValue(int value) {
        mPlaySeekBar.setSelectedCurValue(value);
        mPlayerTimeTV.setText(TimeUtils.formatSecondTime(value));
        mPlayerDurationTV.setText(TimeUtils.formatSecondTime(value));
    }

    @Override
    public void setSeekbarValue(int selmin, int selcur){
        mPlaySeekBar.setSelectedMinValue(selmin);
        mPlaySeekBar.setSelectedCurValue(selcur);
    }

    @Override
    public void setSeekbarMax(int max){
        mPlaySeekBar.setAbsoluteMaxValue(max);
    }

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
    private void showCutterDialog() {
        final Number minNumber = mPlaySeekBar.getSelectedMinValue();
        final Number maxNumber = mPlaySeekBar.getSelectedMaxValue();
        if (maxNumber.intValue() <= minNumber.intValue()) {
            Toast.makeText(getActivity(),
                    getString(R.string.dialog_cutter_warning_length),
                    Toast.LENGTH_LONG).show();
            return;
        }
        final EditText et_fileName = new EditText(getActivity());
        new XfDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_cutter_msg))
                .setView(et_fileName, ViewUtils.dp2px(getActivity(), 10), ViewUtils.dp2px(getActivity(), 10),
                        ViewUtils.dp2px(getActivity(), 10), ViewUtils.dp2px(getActivity(), 10))
                .setPositiveButton(getString(R.string.dialog_btn_sure),
                        new DialogInterface.OnClickListener() {
                            /**
                             * 设置铃声
                             */
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                final String newFileNameStr = et_fileName
                                        .getText().toString().trim();
                                new Thread() {
                                    public void run() {
                                        try {
                                            cutterRingtone(newFileNameStr,
                                                    minNumber.intValue(),
                                                    maxNumber.intValue());
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                    }
                                }.start();
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_btn_cancel), null)
                .create().show();
    }

    /**
     * 剪切音乐
     */
    private void cutterRingtone(String fileName, int minValue, int maxValue) {
        Mp3Fenge helper = new Mp3Fenge(new File(mSelMusicPath));
        if (FileUtils.bFolder(RING_FOLDER)) {
            if (!TextUtils.isEmpty(fileName)) {
                String rangPath = RING_FOLDER + "/" + fileName + RING_FORMAT;
                if (helper.generateNewMp3ByTime(new File(rangPath), minValue, maxValue)) {
                    Message msg = new Message();
                    msg.what = CUTTER_SUCCESS;
                    Bundle b = new Bundle();
                    b.putString(RANG_PATH, rangPath);
                    msg.setData(b);
                    mPlayerProgressHandler.sendMessage(msg);
                }
            }
        }
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
//                if (mPresenter.isPlaying()) {
//                    // 暂停
//                    mPresenter.pause();
//                    mPlayerProgressHandler.removeMessages(UPDATE_PLAY_PROGRESS);
//                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{
//                                android.Manifest.permission.RECORD_AUDIO}, 1);
//                    } else
//                        mVisualView.setEnabled(false);
//                    mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
//                } else {
//                    // 播放
//                    if (TextUtils.isEmpty(mSelMusicPath)) {
//                        Toast.makeText(getActivity(),
//                                getString(R.string.dialog_cutter_warning_sel),
//                                Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    mChangStatusHandler.sendEmptyMessage(STATUS_PLAYING);
//                    Number tmpNumber = mPlaySeekBar.getSelectedCurValue();
//                    mPresenter.seekTo(tmpNumber.intValue());
//                    mPresenter.play();
//                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{
//                                android.Manifest.permission.RECORD_AUDIO}, 1);
//                    } else
//                        mVisualView.setEnabled(true);
//                    Message message = new Message();
//                    message.what = UPDATE_PLAY_PROGRESS;
//                    mPlayerProgressHandler.sendMessage(message);
//                }
//
//            }
        }});

        /**
         * 剪切音乐
         */
        mCutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelMusice())
                    showCutterDialog();
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
                    mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
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
                    mChangStatusHandler.sendEmptyMessage(STATUS_PLAYING);
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

        /**
         * 重置功能
         */
        // mResetBtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // if(isSelMusice()){
        // mPlaySeekBar.setSelectedMinValue(0);
        // mPlaySeekBar.setSelectedMaxValue(myMediaPlayer.getDuration());
        // }
        // }
        // });
    }


    /**
     * 选择文件返回
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CANCELED) {
//            return;
//        }
//        // 文件选择返回结果
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//            mSelMusicPath = data
//                    .getStringExtra(FileChooserActivity.EXTRA_FILE_CHOOSER);
//            try {
//                if (!TextUtils.isEmpty(mSelMusicPath)) {
//                    mPlaySeekBar.setSelectedMinValue(0);
//                    mPlaySeekBar.setSelectedCurValue(0);
//                    mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
//
//                    mPresenter.pause();
//                    mPresenter.reset();
//                    mPresenter.setDataSource(mSelMusicPath);
//                    mPresenter.prepare();
//                    mPlaySeekBar.setAbsoluteMaxValue(mPresenter
//                            .getDuration());
//                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{
//                                android.Manifest.permission.RECORD_AUDIO}, 1);
//                    } else
//                        mVisualView.link(mPresenter.getMediaPlayer());
//                    addBarGraphRenderers();
//                    // String albumPic = SystemTools.getImage(mSelMusicPath,
//                    // PlayerMainActivity.this);
//                    // Toast.makeText(PlayerMainActivity.this, "path:"+albumPic,
//                    // Toast.LENGTH_LONG).show();
//                    // if(albumPic!=null){
//                    // Bundle b = new Bundle();
//                    // b.putString("setPIC", albumPic);
//                    // Message msg = new Message();
//                    // msg.what = 100;
//                    // msg.setData(b);
//                    // mPlayerProgressHandler.sendMessage(msg);
//                    // }
//
//                }
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (SecurityException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * ⑨重写onRequestPermissionsResult方法
     * 获取动态权限请求的结果,再开启录制音频
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(getActivity(), "用户拒绝了权限", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.pause();
        mPlayerProgressHandler.removeMessages(UPDATE_PLAY_PROGRESS);
        mChangStatusHandler.sendEmptyMessage(STATUS_PAUSE);
    }
}
