package com.zyl.mp3cutter.other;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.databinding.ActivitySplashBinding;

/**
 * Description: 欢迎页
 * Created by zouyulong on 2017/11/22.
 * Person in charge :  zouyulong
 */
public class SplashActivity extends AppCompatActivity {
    private TextView mTitleTv;
    private ActivitySplashBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "Avenir LT 95 Black.ttf");
        mTitleTv.setTypeface(typeface);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 3000);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash_cutter);
//        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//            public void onGenerated(Palette palette) {
//                // Use generated instance
//                Palette.Swatch vibrant = palette.getVibrantSwatch();
//                if (vibrant != null) {
//                    mBinding.rlMain.setBackgroundColor(vibrant.getRgb());
//                }
//            }
//        });
    }
}
