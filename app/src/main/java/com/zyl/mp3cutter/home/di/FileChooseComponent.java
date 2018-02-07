package com.zyl.mp3cutter.home.di;

import com.zyl.mp3cutter.common.app.di.ActivityScope;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.home.ui.FileChooserActivity;

import dagger.Component;

/**
 * Description: activity presenter dagger2 component 类
 * Created by zouyulong on 2017/10/22.
 */
@ActivityScope
@Component(modules = FileChooseModule.class, dependencies = AppComponent.class)
public interface FileChooseComponent {
    void inject(FileChooserActivity activity);
}
