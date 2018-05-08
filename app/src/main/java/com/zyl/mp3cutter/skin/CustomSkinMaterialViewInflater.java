package com.zyl.mp3cutter.skin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import skin.support.design.app.SkinMaterialViewInflater;

/**
 * Description: com.zyl.mp3cutter.skin
 * Created by zouyulong on 2018/5/9.
 * Job number:147490
 * Phone : 15810880928
 * Email : zouyulong@syswin.com
 * Person in charge :  zouyulong
 */

public class CustomSkinMaterialViewInflater extends SkinMaterialViewInflater {
    @Override
    public View createView(@NonNull Context context, final String name, @NonNull AttributeSet attrs) {
        View view = super.createView(context, name, attrs);
        if("android.support.design.widget.NavigationView".equals(name)) {
            view = new CustomSkinMaterialNavigationView(context, attrs);
        }
        return view;
    }
}
