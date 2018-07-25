package com.zyl.mp3cutter.common.base;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyl.mp3cutter.R;

/**
 * Description: loading dialog
 * Created by zouyulong on 2018/6/20.
 * Job number:147490
 * Phone : 15810880928
 * Email : zouyulong@syswin.com
 * Person in charge :  zouyulong
 */
public class MyLoadingDialog extends DialogFragment {
    private Context mContext;
    private TextView loadingText;
    private String def_txt_show;
    private ObjectAnimator objectAnimator;

//    public MyLoadingDialog(Context context) {
//        super(context, R.style.dialog_normal);
//        this.mContext = context;
//        this.setContentView(layout.loading);
//        this.setProperty();
//        this.setCancelable(false);
//        this.loadingText = (TextView)this.findViewById(id.loading_text);
//        this.loading_progress = (MoveRoundView)this.findViewById(id.loading_progress);
//        this.def_txt_show = this.loadingText.getText().toString();
//    }

    public MyLoadingDialog(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_daymusic, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.fl_loading_main);
        ImageView view1 = (ImageView) view.findViewById(R.id.loading_dayimage) ;
        RotateAnimation rotateAnimation = new RotateAnimation(0,360, 1, 0.5F, 1, 0.5F );
        rotateAnimation.setDuration(5000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.INFINITE);
        view1.startAnimation(rotateAnimation);
    }

    public void setLoadingMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            this.loadingText.setText(message);
        }

    }
    
//    public void show() {
//        super.show();
//        RotationParams rotationParams = new RotationParams(this.loading_progress);
//        rotationParams.setDuration(2000);
//        rotationParams.setStartDegrees(0.0F);
//        rotationParams.setEndDegrees(360.0F);
//        rotationParams.setRepeatCount(-1);
//        this.objectAnimator = AnimationForViewUtil.rotationAsy(rotationParams);
//    }

//    public void show(String message) {
//        super.show();
//        if (!TextUtils.isEmpty(message)) {
//            this.loadingText.setText(message);
//        } else {
//            this.reset();
//        }
//
//        this.show();
//    }

//    private void reset() {
//        this.loadingText.setText(this.def_txt_show);
//    }

//    public void dismiss() {
//        super.dismiss();
//        this.clear();
//    }

//    public void cancel() {
//        super.cancel();
//        this.clear();
//    }
//
//    private void clear() {
//        this.loading_progress.stop();
//        if (this.objectAnimator != null) {
//            if (Looper.myLooper() == null) {
//                this.loading_progress.post(new Runnable() {
//                    public void run() {
//                        IssLoadingDialog.this.objectAnimator.cancel();
//                    }
//                });
//                return;
//            }
//
//            this.objectAnimator.cancel();
//        }
//
//    }
//
//    private void setProperty() {
//        Window window = this.getWindow();
//        WindowManager.LayoutParams p = window.getAttributes();
//        Display d = this.getWindow().getWindowManager().getDefaultDisplay();
//        p.height = d.getHeight() * 1;
//        p.width = d.getWidth() * 1;
//        window.setAttributes(p);
//    }

    /**
     * 判断弹窗是否显示
     * @return
     */
    public boolean isShowing(){
        return getDialog() != null && getDialog().isShowing();
    }
}
