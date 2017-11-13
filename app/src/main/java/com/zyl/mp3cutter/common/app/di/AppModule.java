package com.zyl.mp3cutter.common.app.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Description: com.zyl.mp3cutter.common.app.di
 * Created by zouyulong on 2017/11/7.
 * Phone : 15810880928
 * Person in charge :  zouyulong
 */
@Module
public class AppModule {
    private Application mApplication;
    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return mApplication;
    }
}
