package com.zyl.mp3cutter.common.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Description: com.zyl.mp3cutter.common.ui.view
 * Created by zouyulong on 2018/4/7.
 */

public class ScrollInterceptScrollView extends ScrollView {
    private int downX, downY;
    private int mTouchSlop;

    public ScrollInterceptScrollView(Context context) {
        this(context, null);
    }

    public ScrollInterceptScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
//        getParent().requestDisallowInterceptTouchEvent(true);

    }

    public ScrollInterceptScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        getParent().requestDisallowInterceptTouchEvent(true);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollInterceptScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        com.orhanobut.logger.Logger.d(action);
        return true;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                downX = (int) ev.getRawX();
//                downY = (int) ev.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int moveY = (int) ev.getRawY();
//                // 判断是否滑动，若滑动就拦截事件
//                if (Math.abs(moveY - downY) > mTouchSlop) {
//                    return true;
//                }
//                break;
//            default:
//                break;
//        }

//        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        com.orhanobut.logger.Logger.d("onTouchEvent" + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(ev);
    }
}