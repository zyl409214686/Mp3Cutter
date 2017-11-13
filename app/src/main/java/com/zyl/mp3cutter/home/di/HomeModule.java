package com.zyl.mp3cutter.home.di;

import com.zyl.mp3cutter.home.presenter.HomeContract;

import dagger.Module;
import dagger.Provides;

/**
 * Description: degger2 module类
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
@Module
public class HomeModule {
    private HomeContract.View view;
    public HomeModule(HomeContract.View view){
        this.view = view;
    }
    @Provides
    public HomeContract.View providerHomeView(){
        return this.view;
    }
}
