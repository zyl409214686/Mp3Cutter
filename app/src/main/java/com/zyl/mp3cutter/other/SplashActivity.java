package com.zyl.mp3cutter.other;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.databinding.ActivitySplashBinding;

/**
 * Description: 欢迎页
 * Created by zouyulong on 2017/11/22.
 * Person in charge :  zouyulong
 */
public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, Color.TRANSPARENT);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        setListener();
    }

    private void setListener() {
        ValueAnimator animator = new ValueAnimator().ofFloat(0f, 1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBinding.lottieImageview.setProgress(Float.parseFloat
                        (animation.getAnimatedValue().toString()));
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Logger.d("onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Logger.d("onAnimationEnd");
                goToMainActivity();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Logger.d("onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Logger.d("onAnimationRepeat");
            }
        });
        animator.start();
    }

    private void goToMainActivity(){
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
