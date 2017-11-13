package com.zyl.mp3cutter.common.app.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Description: com.zyl.mp3cutter.common.app
 * Created by zouyulong on 2017/11/7.
 * Email : zouyulong@syswin.com
 * Person in charge :  zouyulong
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Application Application();
}
