package com.zyl.mp3cutter.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.app.di.AppComponent;

import javax.inject.Inject;


/**
 * MVPPlugin
 */

public abstract class BaseActivity<V extends IBaseView,T extends BasePresenter<V>>
        extends AppCompatActivity implements IBaseView {
    protected MyApplication myApplication;

    @Inject
    public T mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPresenter= getInstance(this,1);
        setContentView(initView());
        myApplication = (MyApplication) getApplication();
        ComponentInject(myApplication.getAppComponent());//依赖注入
        initData();
    }

    @Override
    public Context getContext(){
        return this;
    }

    /**
     * 依赖注入的入口
     */
    protected abstract void ComponentInject(AppComponent appComponent);

    protected abstract View initView();

    protected abstract void initData();

//    public  <T> T getInstance(Object o, int i) {
//        try {
//            return ((Class<T>) ((ParameterizedType) (o.getClass()
//                    .getGenericSuperclass())).getActualTypeArguments()[i])
//                    .newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
