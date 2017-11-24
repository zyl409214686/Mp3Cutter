package com.zyl.mp3cutter.home.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.databinding.ActivityFilechooserShowBinding;
import com.zyl.mp3cutter.home.bean.MusicInfo;
import com.zyl.mp3cutter.home.bean.MusicInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


//import android.widget.TextView;

public class FileChooserActivity extends Activity {

    private ListView mListView;
    // private View mBackView;
    private View mBtExit;
    // private TextView mTvPath ;

    private String mSdcardRootPath;
    private String mLastFilePath;
    private ArrayList<MusicInfo> mFileLists = new ArrayList<MusicInfo>();
    private FileChooserAdapter mAdatper;
    public static final String EXTRA_FILE_CHOOSER = "file_chooser";
    private ProgressDialog dialog;
    private Handler mHandler = new Handler();
    MusicInfoDao mDao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityFilechooserShowBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_filechooser_show);

        mSdcardRootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        mBtExit = findViewById(R.id.btExit);
        mBtExit.setOnClickListener(mClickListener);

        mListView = (ListView) findViewById(R.id.gvFileChooser);
        mListView.setEmptyView(findViewById(R.id.tvEmptyHint));
        mListView.setOnItemClickListener(mItemClickListener);
        mAdatper = new FileChooserAdapter(this);
        mListView.setAdapter(mAdatper);
        refreshData();
    }

    private void refreshData() {
        mDao = MyApplication.getInstances().
                getDaoSession().getMusicInfoDao();
        dialog = ProgressDialog.show(FileChooserActivity.this, "提示",
                "音乐文件扫描中...");
        new Thread() {
            public void run() {
                List<MusicInfo> datas = mDao.loadAll();
                if (datas.size() > 0) {
                    mFileLists.clear();
                    mFileLists.addAll(datas);
                } else {
                    updateFileItems(mSdcardRootPath);
                    mDao.deleteAll();
                    mDao.insertInTx(mFileLists);
                }
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        dialog.dismiss();
                        // When first enter , the object of mAdatper don't
                        // initialized
                        if (mAdatper != null) {
                            mAdatper.setData(mFileLists);
                            mAdatper.notifyDataSetChanged();
                        }
                    }
                });
            }

            ;
        }.start();

    }

    private void updateFileItems(String filePath) {
        mLastFilePath = filePath;
        // mTvPath.setText(mLastFilePath);

        if (mFileLists == null)
            mFileLists = new ArrayList<>();
        // if(!mFileLists.isEmpty())
        // mFileLists.clear() ;

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
            FileChooserAdapter.FileInfo fileInfo = new FileChooserAdapter.FileInfo(fileAbsolutePath, fileName,
                    isDirectory);
            if (fileInfo.isDirectory())
                updateFileItems(fileInfo.getFilePath());
            else if (fileInfo.isMUSICFile()) {
                String path = fileInfo.getFilePath();
                file = new File(path);
                String size = FileUtils.getFormatFileSizeForFile(file);
                MusicInfo music = new MusicInfo(null, fileInfo.getFilePath(),
                        fileInfo.getFileName(), size);
                mFileLists.add(music);
            }
        }
    }

    private File[] folderScan(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }

    private OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btExit:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int position, long id) {
            MusicInfo fileInfo = (MusicInfo) (((FileChooserAdapter) adapterView
                    .getAdapter()).getItem(position));
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FILE_CHOOSER, fileInfo.getFilepath());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

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
}