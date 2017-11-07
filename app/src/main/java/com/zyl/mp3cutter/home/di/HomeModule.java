package com.zyl.mp3cutter.home.di;

import android.media.MediaPlayer;

import dagger.Module;
import dagger.Provides;

/**
 * Description: degger2 module类
 * Created by zouyulong on 2017/10/22.
 * Person in charge :  zouyulong
 */
@Module
public class HomeModule {
    @Provides
    public MediaPlayer providerMediaPlayer(){
        return new MediaPlayer();
    }
}
