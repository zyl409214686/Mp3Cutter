package com.zyl.mp3cutter.common.app;

import android.app.Application;

import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.app.di.AppModule;
import com.zyl.mp3cutter.common.app.di.DaggerAppComponent;

/**
 * Description: 自定义application
 * Created by zouyulong on 2017/11/7.
 * Person in charge :  zouyulong
 */

public class MyApplication extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
