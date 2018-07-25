package com.zyl.mp3cutter.common.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;
import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.app.di.AppComponent;

import javax.inject.Inject;


/**
 * MVPPlugin
 */

public abstract class BaseActivity<V extends IBaseView,T extends BasePresenter<V>, B extends ViewDataBinding>
        extends AppCompatActivity implements IBaseView {
    protected MyApplication myApplication;
    protected MyLoadingDialog mLoadingDialog;
    @Inject
    public T mPresenter;

    protected  B mDataBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, initLayoutResId());
        myApplication = (MyApplication) getApplication();
        ComponentInject(myApplication.getAppComponent());//依赖注入
        initData(savedInstanceState);
    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MyLoadingDialog();
        } else if (mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }

        mLoadingDialog.show(getSupportFragmentManager(), "loadingdialog");
    }

    @Override
    public Context getContext(){
        return this;
    }

    /**
     * 依赖注入的入口
     */
    protected abstract void ComponentInject(AppComponent appComponent);

    protected abstract int initLayoutResId();

    protected abstract void initData(Bundle savedInstanceState);

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
