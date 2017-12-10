package com.zyl.mp3cutter.home.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.zyl.mp3cutter.home.bean.MusicInfo;
import com.zyl.mp3cutter.home.bean.MusicInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Description: mp3文件选择页
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
public class FileChooserActivity extends AppCompatActivity implements OnClickListener {

    private RecyclerView mRecyclerView;
    private CommonAdapter mAdapter;
    private String mSdcardRootPath;
    private String mLastFilePath;
    private ArrayList<MusicInfo> mFileList = new ArrayList<>();
    private ArrayList<MusicInfo> mMusicList = new ArrayList<>();
    public static final String EXTRA_FILE_CHOOSER = "file_chooser";
    private ProgressDialog dialog;
    private Handler mHandler = new Handler();
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
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshData(final boolean isfocus) {
        if (mLoadingView.isShown())
            return;
        mLoadingView.setVisibility(View.VISIBLE);
        startLoadingAnim();
//        dialog = ProgressDialog.show(FileChooserActivity.this, "提示",
//                "音乐文件扫描中...");
        new Thread() {
            public void run() {
                List<MusicInfo> datas = mDao.loadAll();
                if (datas.size() > 0 && !isfocus) {
                    mFileList.clear();
                    mFileList.addAll(datas);
                } else {
                    mFileList.clear();
                    updateFileItems(mSdcardRootPath);
                    mDao.deleteAll();
                    mDao.insertInTx(mFileList);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMusicList.clear();
                        mMusicList.addAll(mFileList);
                        mLoadingView.setVisibility(View.GONE);
                        stopLoadingAnim();
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    private void startLoadingAnim() {
        mMoveAnim  = ObjectAnimator.ofFloat(mLoadingView, "translationX", 0f, mUpdateBtnLeft);
        mMoveAnim.setDuration(2000);
        mMoveAnim.setRepeatCount(ValueAnimator.INFINITE);
        mMoveAnim.start();
    }

    private void stopLoadingAnim(){
        mMoveAnim.cancel();
    }

    private void updateFileItems(String filePath) {
        mLastFilePath = filePath;
        // mTvPath.setText(mLastFilePath);

        if (mFileList == null)
            mFileList = new ArrayList<>();
        // if(!mFileList.isEmpty())
        // mFileList.clear() ;

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
                updateFileItems(fileInfo.getFilePath());
            else if (fileInfo.isMUSICFile()) {
                String path = fileInfo.getFilePath();
                file = new File(path);
                String size = FileUtils.getFormatFileSizeForFile(file);
                MusicInfo music = new MusicInfo(null, fileInfo.getFilePath(),
                        fileInfo.getFileName(), size);
                mFileList.add(music);
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
        if (dialog != null)
            dialog.dismiss();
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

    // =========================
    // Model
    // =========================
    public static class FileInfo {
        private FileType fileType;
        private String fileName;
        private String filePath;

        public FileInfo(String filePath, String fileName, boolean isDirectory) {
            this.filePath = filePath;
            this.fileName = fileName;
            fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
        }

        public boolean isMUSICFile() {
            if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
                return false;
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            if (!isDirectory() && MUSIC_SUFFIX.contains(fileSuffix))
                return true;
            else
                return false;
        }

        public boolean isDirectory() {
            if (fileType == FileType.DIRECTORY)
                return true;
            else
                return false;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public String toString() {
            return "FileInfo [fileType=" + fileType + ", fileName=" + fileName
                    + ", filePath=" + filePath + "]";
        }
    }

    private static ArrayList<String> MUSIC_SUFFIX = new ArrayList<String>();

    static {
        MUSIC_SUFFIX.add(".mp3");
        // PPT_SUFFIX.add(".pptx");
    }

    enum FileType {
        FILE, DIRECTORY;
    }
}