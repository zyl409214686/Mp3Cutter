package com.zyl.mp3cutter.common.app;

import android.app.Application;

import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.app.di.AppModule;
import com.zyl.mp3cutter.common.app.di.DaggerAppComponent;
import com.zyl.mp3cutter.common.utils.LogUtils;
import com.zyl.mp3cutter.home.bean.MyObjectBox;
import com.zyl.mp3cutter.skin.CustomSkinMaterialViewInflater;

import io.objectbox.BoxStore;
import skin.support.SkinCompatManager;

//import com.zyl.mp3cutter.home.bean.DaoMaster;
//import com.zyl.mp3cutter.home.bean.DaoSession;

//import com.zyl.mp3cutter.DaoMaster;
//import com.zyl.mp3cutter.DaoSession;

/**
 * Description: 自定义application
 * Created by zouyulong on 2017/11/7.
 */

public class MyApplication extends Application {
    private AppComponent mAppComponent;
    public static MyApplication instances;
    private BoxStore mBoxStore;
    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        initDatabase();
//        CoverLoader.getInstance().init(instances);
        //logger
        if(LogUtils.isApkDebugable(this)) {
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
        //umeng analytics
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        SkinCompatManager.withoutActivity(this)                         // 基础控件换肤初始化
                .addInflater(new CustomSkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }


    public static MyApplication getInstances() {
        return instances;
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        mBoxStore = MyObjectBox.builder().androidContext(this).build();
    }

    public BoxStore getBoxStore(){
        return mBoxStore;
    }


    private static Gson gson;

    public static Gson gsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
