package com.zyl.mp3cutter.home.di;

import com.zyl.mp3cutter.common.app.di.ActivityScope;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.home.ui.HomeFragment;

import dagger.Component;

/**
 * Description: Home presenter dagger2 component 类
 * Created by zouyulong on 2017/10/22.
 * Job number:147490
 * Person in charge :  zouyulong
 */
@ActivityScope
@Component(modules = HomeModule.class, dependencies = AppComponent.class)
public interface HomeComponent {
    void inject(HomeFragment fragment);
}
