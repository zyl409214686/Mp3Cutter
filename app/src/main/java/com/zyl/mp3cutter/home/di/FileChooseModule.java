package com.zyl.mp3cutter.home.di;

import com.zyl.mp3cutter.home.presenter.FileChooseContract;

import dagger.Module;
import dagger.Provides;

/**
 * Description: degger2 module类
 * Created by zouyulong on 2018/2/2.
 * Person in charge :  zouyulong
 */
@Module
public class FileChooseModule {
    private FileChooseContract.View view;
    public FileChooseModule(FileChooseContract.View view){
        this.view = view;
    }
    @Provides
    public FileChooseContract.View providerHomeView(){
        return this.view;
    }
}
