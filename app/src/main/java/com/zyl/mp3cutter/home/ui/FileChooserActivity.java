package com.zyl.mp3cutter.home.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.jaeger.library.StatusBarUtil;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.utils.DensityUtils;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.common.utils.ScreenUtils;
import com.zyl.mp3cutter.databinding.ActivityFilechooserShowBinding;
import com.zyl.mp3cutter.home.bean.FileInfo;
import com.zyl.mp3cutter.home.bean.MusicInfo;
import com.zyl.mp3cutter.home.bean.MusicInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Description: mp3文件选择页
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
public class FileChooserActivity extends AppCompatActivity implements OnClickListener {

    private RecyclerView mRecyclerView;
    private CommonAdapter mAdapter;
    private String mSdcardRootPath;
    private ArrayList<MusicInfo> mMusicList = new ArrayList<>();
    public static final String EXTRA_FILE_CHOOSER = "file_chooser";
    private AVLoadingIndicatorView mLoadingView;
    private MusicInfoDao mDao;
    private ObjectAnimator mMoveAnim;
    private ActivityFilechooserShowBinding mBinding;
    private int mUpdateBtnLeft;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.transition.Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.explode);
            getWindow().setEnterTransition(transition);
        }
        StatusBarUtil.setColor(this, Color.TRANSPARENT);
        mBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_filechooser_show);
        mBinding.btnUpdate.setOnClickListener(this);
        mBinding.btnUpdate.measure(0, 0);
        mUpdateBtnLeft = ScreenUtils.getScreenSize(this)[0] -
                mBinding.btnUpdate.getMeasuredWidth() - DensityUtils.dp2px(this ,10);
        initToolbar();
        mSdcardRootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        mRecyclerView = mBinding.rlMusice;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonAdapter<MusicInfo>(this, R.layout.item_musicfile, mMusicList) {
            @Override
            protected void convert(ViewHolder holder, final MusicInfo musicInfo, int position) {
                holder.setText(R.id.tv_name, musicInfo.getFilename());
                holder.setText(R.id.tv_size, musicInfo.getFileSize());
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickItem(musicInfo);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mDao = MyApplication.getInstances().
                getDaoSession().getMusicInfoDao();
        mLoadingView = mBinding.aviLoading;
        refreshData(false);
    }

    private void clickItem(MusicInfo info) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_FILE_CHOOSER, info.getFilepath());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshData(final boolean isforce) {
        if (mLoadingView.isShown())
            return;
        startLoadingAnim();
        loadFile(isforce);
    }

    private void loadFile(final boolean isforce){
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                List<MusicInfo> datas = mDao.loadAll();
                if (datas.size() > 0 && !isforce) {
                } else {
                    datas = new ArrayList<>();
                    updateFileItems(datas, mSdcardRootPath);
                    mDao.deleteAll();
                    if(datas!=null) {
                        mDao.insertInTx(datas);
                    }
                }
                e.onNext(datas);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<MusicInfo>>() {
                    @Override
                    public void accept(List<MusicInfo> datas) throws Exception {
                        mMusicList.clear();
                        mMusicList.addAll(datas);
                        stopLoadingAnim();
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void startLoadingAnim() {
        mLoadingView.setVisibility(View.VISIBLE);
        mMoveAnim  = ObjectAnimator.ofFloat(mLoadingView, "translationX", 0f, mUpdateBtnLeft);
        mMoveAnim.setDuration(2000);
        mMoveAnim.setRepeatCount(ValueAnimator.INFINITE);
        mMoveAnim.start();
    }

    private void stopLoadingAnim(){
        mMoveAnim.cancel();
        mLoadingView.setVisibility(View.GONE);
    }

    private void updateFileItems(List<MusicInfo> list, String filePath) {
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
                updateFileItems(list, fileInfo.getFilePath());
            else if (fileInfo.isMUSICFile()) {
                String path = fileInfo.getFilePath();
                file = new File(path);
                String size = FileUtils.getFormatFileSizeForFile(file);
                MusicInfo music = new MusicInfo(null, fileInfo.getFilePath(),
                        fileInfo.getFileName(), size);
                list.add(music);
            }
        }
    }

    private File[] folderScan(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            backProcess();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backProcess() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_update:
                refreshData(true);
                break;
        }
    }
}