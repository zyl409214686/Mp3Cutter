package com.zyl.mp3cutter.home.di;

import com.zyl.mp3cutter.home.presenter.HomePresenter;

import dagger.Component;

/**
 * Description: Home presenter dagger2 component 类
 * Created by zouyulong on 2017/10/22.
 * Job number:147490
 * Person in charge :  zouyulong
 */
@Component(modules = HomeModule.class)
public interface HomeComponent {
    void inject(HomePresenter presenter);
}
