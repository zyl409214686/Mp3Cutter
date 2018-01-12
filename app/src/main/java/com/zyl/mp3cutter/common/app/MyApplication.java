package com.zyl.mp3cutter.common.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.app.di.AppModule;
import com.zyl.mp3cutter.common.app.di.DaggerAppComponent;
import com.zyl.mp3cutter.common.utils.LogUtils;
import com.zyl.mp3cutter.home.bean.DaoMaster;
import com.zyl.mp3cutter.home.bean.DaoSession;

import skin.support.SkinCompatManager;
import skin.support.design.app.SkinMaterialViewInflater;

//import com.zyl.mp3cutter.DaoMaster;
//import com.zyl.mp3cutter.DaoSession;

/**
 * Description: 自定义application
 * Created by zouyulong on 2017/11/7.
 */

public class MyApplication extends Application {
    private AppComponent mAppComponent;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static MyApplication instances;

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        //greendao
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        setDatabase();
        //logger
        if(LogUtils.isApkDebugable(this)) {
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
        //umeng analytics
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        SkinCompatManager.withoutActivity(this)                         // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
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
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
         mHelper = new DaoMaster.DevOpenHelper(this, "local_musics_db", null);
         db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
         mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
