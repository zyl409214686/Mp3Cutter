package com.zyl.mp3cutter.common.ui.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Description: com.zyl.mp3cutter.common.ui.view
 * Created by zouyulong on 2018/4/8.
 */

public class CustomDrawerLayout extends DrawerLayout {
    public CustomDrawerLayout(Context context) {
        super(context);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private int mTouchSlop;
    private float mLastMotionX;
    private float mLastMotionY;

    public CustomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final ViewConfiguration configuration = ViewConfiguration
                .get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            final float x = ev.getX();
            final float y = ev.getY();

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    int xDiff = (int) Math.abs(x - mLastMotionX);
                    int yDiff = (int) Math.abs(y - mLastMotionY);
                    final int x_yDiff = xDiff * xDiff + yDiff * yDiff;

                    boolean xMoved = x_yDiff > mTouchSlop * mTouchSlop;

                    if (xMoved) {
                        if (xDiff > yDiff * 4) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    break;
                default:

                    break;
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
        }
        return false;
    }
}
