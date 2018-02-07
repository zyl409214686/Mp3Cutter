package com.zyl.mp3cutter.home.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.library.StatusBarUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseActivity;
import com.zyl.mp3cutter.common.utils.DensityUtils;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.common.utils.ScreenUtils;
import com.zyl.mp3cutter.databinding.ActivityFilechooserShowBinding;
import com.zyl.mp3cutter.home.bean.MusicInfo;
import com.zyl.mp3cutter.home.di.DaggerFileChooseComponent;
import com.zyl.mp3cutter.home.di.FileChooseModule;
import com.zyl.mp3cutter.home.presenter.FileChooseContract;
import com.zyl.mp3cutter.home.presenter.FileChoosePresenter;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;
import skin.support.widget.SkinCompatToolbar;


/**
 * Description: mp3文件选择页
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
@RuntimePermissions
public class FileChooserActivity extends BaseActivity<FileChooseContract.View, FileChoosePresenter, ActivityFilechooserShowBinding> implements OnClickListener,
FileChooseContract.View{

    private CommonAdapter mAdapter;
    private ArrayList<MusicInfo> mMusicList = new ArrayList<>();
    public static final String EXTRA_FILEPATH_CHOOSER = "filepath_chooser";
    public static final String EXTRA_FILE_CHOOSER = "file_chooser";
    private ObjectAnimator mMoveAnim;
    private int mUpdateBtnLeft;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.transition.Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.explode);
            getWindow().setEnterTransition(transition);
        }
        StatusBarUtil.setColor(this, Color.TRANSPARENT);
        mDataBinding.btnUpdate.setOnClickListener(this);
        mDataBinding.btnUpdate.measure(0, 0);
        mUpdateBtnLeft = ScreenUtils.getScreenSize(this)[0] -
                mDataBinding.btnUpdate.getMeasuredWidth() - DensityUtils.dp2px(this ,10);
        initToolbar();
        mDataBinding.rlMusice.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonAdapter<MusicInfo>(this, R.layout.item_musicfile, mMusicList) {
            @Override
            protected void convert(ViewHolder holder, final MusicInfo musicInfo, int position) {
                holder.setText(R.id.tv_name, musicInfo.getTitle());
                holder.setText(R.id.tv_size, FileUtils.formetFileSize(musicInfo.getFileSize()));
                if(TextUtils.isEmpty(musicInfo.getCoverPath())){
                    holder.setImageDrawable(R.id.iv_icon, getResources().getDrawable(R.mipmap.music_icon));
                }
                else {
                    RequestOptions options = new RequestOptions().placeholder(R.mipmap.music_icon);
                    Glide.with(FileChooserActivity.this).load(musicInfo.getCoverPath())
                            .apply(options).into((ImageView) holder.getView(R.id.iv_icon));
                }
//                holder.setImageBitmap(R.id.iv_icon, CoverLoader.getInstance().loadThumbnail(musicInfo));
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickItem(musicInfo);
                    }
                });
            }
        };
        mDataBinding.rlMusice.setAdapter(mAdapter);
        mDataBinding.rlMusice.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        FileChooserActivityPermissionsDispatcher.refreshDataWithPermissionCheck(this, false);
    }

    @Override
    protected void ComponentInject(AppComponent appComponent) {
        DaggerFileChooseComponent
                .builder()
                .appComponent(appComponent)
                .fileChooseModule(new FileChooseModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_filechooser_show;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void clickItem(MusicInfo info) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_FILEPATH_CHOOSER, info.getFilepath());
        intent.putExtra(EXTRA_FILE_CHOOSER, info);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initToolbar() {
        SkinCompatToolbar toolbar = (SkinCompatToolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.theme_color);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void refreshData(final boolean isforce) {
        if (mDataBinding.aviLoading.isShown())
            return;
        startLoadingAnim();
        mPresenter.loadFile(isforce);
    }

    private void startLoadingAnim() {
        mDataBinding.aviLoading.setVisibility(View.VISIBLE);
        mMoveAnim  = ObjectAnimator.ofFloat(mDataBinding.aviLoading, "translationX", 0f, mUpdateBtnLeft);
        mMoveAnim.setDuration(2000);
        mMoveAnim.setRepeatCount(ValueAnimator.INFINITE);
        mMoveAnim.start();
    }

    private void stopLoadingAnim(){
        mMoveAnim.cancel();
        mDataBinding.aviLoading.setVisibility(View.GONE);
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
                FileChooserActivityPermissionsDispatcher.refreshDataWithPermissionCheck(this, true);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FileChooserActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onRecordNeverAskAgain() {
        Toast.makeText(FileChooserActivity.this, getResources().getString(R.string.filechoose_permission_denied),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getMusicList(List<MusicInfo> musiclist) {
        mMusicList.clear();
        mMusicList.addAll(musiclist);
        stopLoadingAnim();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}