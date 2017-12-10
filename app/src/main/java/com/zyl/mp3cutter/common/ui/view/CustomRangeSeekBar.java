package com.zyl.mp3cutter.common.ui.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zyl.mp3cutter.R;


/**
 * 自定义范围选取滑块工具栏
 *
 * @author zouyulong
 */
public class CustomRangeSeekBar extends View {
    private final Paint mPaint = new Paint();
    //滑块bitmap
    private Bitmap mThumbImage;
    //progress bar 背景
    private Bitmap mProgressBarBg;
    //progress bar 选中背景
    private Bitmap mProgressBarSelBg;
    private float mThumbWidth;
    private float mThumbHalfWidth;
    private float mThumbHalfHeight;
    //seekbar 进度条高度
    private float mProgressBarHeight;
    //宽度左右padding
    private float mWidthPadding;

    //最小值（绝对）
    private float mAbsoluteMinValue;
    //最大值（绝对）
    private float mAbsoluteMaxValue;

    //已选标准（占滑动条百分比）最小值
    private double mPercentMinValue = 0d;
    //已选标准（占滑动条百分比）最大值
    private double mPercentMaxValue = 1d;

    //当前事件处理的thumb滑块
    private Thumb mPressedThumb = null;
    //滑块事件
    private ThumbListener mThumbListener;

    private RectF mProgressBarRect;
    private RectF mProgressBarSelRect;
    //是否可以滑动
    private boolean mIsEnable = true;
    //最大值和最小值之间要求的最小范围绝对值
    private float mBetweenAbsoluteValue;
    //空间最小宽度
    private final int MIN_WIDTH = 200;
    //进度文本显示格式-数字格式
    public static final int HINT_FORMAT_NUMBER = 0;
    //进度文本显示格式-时间格式
    public static final int HINT_FORMAT_TIME = 1;
    private int mProgressTextFormat;
    //文本高度
    private int mWordHeight;
    //文本字体大小
    private float mWordSize;
    public CustomRangeSeekBar(Context context) {
        super(context);
    }

    public CustomRangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomRangeSeekBar, 0, 0);
        this.mAbsoluteMinValue = new Float(a.getFloat(R.styleable.CustomRangeSeekBar_absoluteMin, (float) 0.0));
        this.mAbsoluteMaxValue = new Float(a.getFloat(R.styleable.CustomRangeSeekBar_absolutemMax, (float) 100.0));
        mThumbImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.CustomRangeSeekBar_thumbImage, R.mipmap.btn_seekbar_normal));
        mProgressBarBg = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.CustomRangeSeekBar_progressBarBg, R.mipmap.seekbar_bg));
        mProgressBarSelBg = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.CustomRangeSeekBar_progressBarSelBg, R.mipmap.seekbar_sel_bg));
        mBetweenAbsoluteValue = a.getFloat(R.styleable.CustomRangeSeekBar_betweenAbsoluteValue, 0);
        mProgressTextFormat = a.getInt(R.styleable.CustomRangeSeekBar_progressTextFormat, HINT_FORMAT_NUMBER);
        mWordSize = a.getDimension(R.styleable.CustomRangeSeekBar_progressTextSize, 16);
        mPaint.setTextSize(mWordSize);
        mThumbWidth = mThumbImage.getWidth();
        mThumbHalfWidth = 0.5f * mThumbWidth;
        mThumbHalfHeight = 0.5f * mThumbImage.getHeight();
        mProgressBarHeight = 0.3f * mThumbHalfHeight;
        //TOOD 提供定义attr
        mWidthPadding = mThumbHalfHeight;
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mWordHeight = (int) (metrics.descent - metrics.ascent);
        setPercentMinValue(0.0);
        setPercentMaxValue(100.0);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mProgressBarRect = new RectF(mWidthPadding, mWordHeight + 0.5f * (h - mWordHeight - mProgressBarHeight),
                w - mWidthPadding, mWordHeight + 0.5f*(h - mWordHeight + mProgressBarHeight));
        mProgressBarSelRect = new RectF(mProgressBarRect);
    }

    /**
     * 设置seekbar 是否接收事件
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mIsEnable = enabled;
    }

    public void setAbsoluteMaxValue(double maxvalue) {
        this.mAbsoluteMaxValue = new Float(maxvalue);
    }

    /**
     * 返回被选择的最小值(绝对值)
     *
     * @return The currently selected min value.
     */
    public float getSelectedAbsoluteMinValue() {
        return percentToAbsoluteValue(mPercentMinValue);
    }

    /**
     * 设置被选择的最小值（绝对值）
     *
     * @param value 最小值的绝对值
     * return 如果最小值与最大值的最小间距达到阈值返回false,正常返回true
     */
    public boolean setSelectedAbsoluteMinValue(float value) {
        boolean status = true;
        if (0 == (mAbsoluteMaxValue - mAbsoluteMinValue)) {
            setPercentMinValue(0d);
        } else {
            float maxValue = percentToAbsoluteValue(mPercentMaxValue);
            if (mBetweenAbsoluteValue>0 && maxValue - value <= mBetweenAbsoluteValue) {
                value = new Float(maxValue - mBetweenAbsoluteValue);
                status = false;
            }
            if(maxValue - value <= 0) {
                status = false;
                value = maxValue;
            }
            setPercentMinValue(absoluteValueToPercent(value));
        }
        return status;
    }

    public float getAbsoluteMaxValue(){
        return mAbsoluteMaxValue;
    }

    /**
     * 返回被选择的最大值（绝对值）.
     */
    public float getSelectedAbsoluteMaxValue() {
        return percentToAbsoluteValue(mPercentMaxValue);
    }

    /**
     * 设置被选择的最大值（绝对值）
     *
     * @param value
     */
    public boolean setSelectedAbsoluteMaxValue(float value) {
        boolean status = true;
        if (0 == (mAbsoluteMaxValue - mAbsoluteMinValue)) {
            setPercentMaxValue(1d);
        } else {
            float minValue = percentToAbsoluteValue(mPercentMinValue);
            if (mBetweenAbsoluteValue>0&&value - minValue <= mBetweenAbsoluteValue) {
                value = new Float(minValue + mBetweenAbsoluteValue);
                status = false;
            }
            if(value - minValue <= 0) {
                status = false;
                value = minValue;
            }
            setPercentMaxValue(absoluteValueToPercent(value));
        }
        return status;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsEnable)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressedThumb = evalPressedThumb(event.getX());
                if (Thumb.MIN.equals(mPressedThumb)) {
                    if (mThumbListener != null)
                        mThumbListener.onClickMinThumb(getSelectedAbsoluteMaxValue(), getSelectedAbsoluteMinValue());
                }
                if (Thumb.MAX.equals(mPressedThumb)) {
                    if (mThumbListener != null)
                        mThumbListener.onClickMaxThumb();
                }
                invalidate();
                //Intercept parent TouchEvent
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPressedThumb != null) {
                    float eventX = event.getX();
                    float maxValue = percentToAbsoluteValue(mPercentMaxValue);
                    float minValue = percentToAbsoluteValue(mPercentMinValue);
                    float eventValue = percentToAbsoluteValue(screenToPercent(eventX));
                    if (Thumb.MIN.equals(mPressedThumb)) {
                        minValue = eventValue;
                        if (mBetweenAbsoluteValue>0 && maxValue - minValue <= mBetweenAbsoluteValue)
                            minValue = new Float((maxValue - mBetweenAbsoluteValue));
//                        setPercentMinValue(screenToPercent(event.getX()));
                        setPercentMinValue(absoluteValueToPercent(minValue));
                        if (mThumbListener != null)
                            mThumbListener.onMinMove(getSelectedAbsoluteMaxValue(), getSelectedAbsoluteMinValue());
                    } else if (Thumb.MAX.equals(mPressedThumb)) {
                        maxValue = eventValue;
                        if (mBetweenAbsoluteValue>0 && maxValue - minValue <= mBetweenAbsoluteValue)
                            maxValue = new Float(minValue + mBetweenAbsoluteValue);
//                        setPercentMaxValue(screenToPercent(event.getX()));
                        setPercentMaxValue(absoluteValueToPercent(maxValue));
                        if (mThumbListener != null)
                            mThumbListener.onMaxMove(getSelectedAbsoluteMaxValue(), getSelectedAbsoluteMinValue());
                    }
                }
                //Intercept parent TouchEvent
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Thumb.MIN.equals(mPressedThumb)) {
                    if (mThumbListener != null)
                        mThumbListener.onUpMinThumb(getSelectedAbsoluteMaxValue(), getSelectedAbsoluteMinValue());
                }
                if (Thumb.MAX.equals(mPressedThumb)) {
                    if (mThumbListener != null)
                        mThumbListener.onUpMaxThumb();
                }
                //Intercept parent TouchEvent
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (Thumb.MIN.equals(mPressedThumb)) {
                    if (mThumbListener != null)
                        mThumbListener.onUpMinThumb(getSelectedAbsoluteMaxValue(), getSelectedAbsoluteMinValue());
                }
                if (Thumb.MAX.equals(mPressedThumb)) {
                    if (mThumbListener != null)
                        mThumbListener.onUpMaxThumb();
                }
                mPressedThumb = null;
                //Intercept parent TouchEvent
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MIN_WIDTH;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = mThumbImage.getHeight() + mWordHeight;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw seek bar background line
        mPaint.setStyle(Style.FILL);
        canvas.drawBitmap(mProgressBarBg, null, mProgressBarRect, mPaint);
        // draw seek bar active range line
        mProgressBarSelRect.left = percentToScreen(mPercentMinValue);
        mProgressBarSelRect.right = percentToScreen(mPercentMaxValue);
        //canvas.drawBitmap(mProgressBarSelBg, mWidthPadding, 0.5f * (getHeight() - mProgressBarHeight), mPaint);
        canvas.drawBitmap(mProgressBarSelBg, null, mProgressBarSelRect, mPaint);
        // draw minimum thumb
        drawThumb(percentToScreen(mPercentMinValue), Thumb.MIN.equals(mPressedThumb), canvas);
        // draw maximum thumb
        drawThumb(percentToScreen(mPercentMaxValue), Thumb.MAX.equals(mPressedThumb), canvas);
        mPaint.setColor(Color.rgb(255, 165, 0));
//        mPaint.setTextSize(DensityUtils.dp2px(getContext(), 16));
        drawThumbMinText(percentToScreen(mPercentMinValue), getSelectedAbsoluteMinValue(), canvas);
        drawThumbMaxText(percentToScreen(mPercentMaxValue), getSelectedAbsoluteMaxValue(), canvas);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("MIN", mPercentMinValue);
        bundle.putDouble("MAX", mPercentMaxValue);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        mPercentMinValue = bundle.getDouble("MIN");
        mPercentMaxValue = bundle.getDouble("MAX");
    }

    /**
     * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
     *
     * @param screenCoord The x-coordinate in screen space where to draw the image.
     * @param pressed     Is the thumb currently in "pressed" state?
     * @param canvas      The canvas to draw upon.
     */
    private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
        canvas.drawBitmap(mThumbImage, screenCoord - mThumbHalfWidth, (mWordHeight + 0.5f * (getHeight()-mWordHeight) - mThumbHalfHeight), mPaint);//pressed ? thumbPressedImage :
    }

    /**
     * 画min滑块值text
     *
     * @param screenCoord
     * @param value
     * @param canvas
     */
    private void drawThumbMinText(float screenCoord, Number value, Canvas canvas) {
        String progress = getProgressStr(value.intValue());
        float progressWidth = mPaint.measureText(progress);
        canvas.drawText(progress, screenCoord - progressWidth/2, mWordSize, mPaint);
    }

    /**
     * 画max滑块值text
     *
     * @param screenCoord
     * @param value
     * @param canvas
     */
    private void drawThumbMaxText(float screenCoord, Number value, Canvas canvas) {
        String progress = getProgressStr(value.intValue());
        float progressWidth = mPaint.measureText(progress);
        canvas.drawText(progress, screenCoord - progressWidth/2, mWordSize
                , mPaint);
    }

    /**
     * 根据touchX, 判断是哪一个thumb(Min or Max)
     *
     * @param touchX 触摸的x在屏幕中坐标（相对于容器）
     */
    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInThumbRange(touchX, mPercentMinValue);
        boolean maxThumbPressed = isInThumbRange(touchX, mPercentMaxValue);
        if (minThumbPressed && maxThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
        } else if (minThumbPressed) {
            result = Thumb.MIN;
        } else if (maxThumbPressed) {
            result = Thumb.MAX;
        }
        return result;
    }

    /**
     * 判断touchX是否在滑块点击范围内
     * @param touchX            需要被检测的 屏幕中的x坐标（相对于容器）
     * @param percentThumbValue 需要检测的滑块x坐标百分比值（滑块x坐标）
     */
    private boolean isInThumbRange(float touchX, double percentThumbValue) {
        return Math.abs(touchX - percentToScreen(percentThumbValue)) <= mThumbHalfWidth;
    }

    /**
     * 设置最小值的百分比值
     */
    public void setPercentMinValue(double value) {
        mPercentMinValue = Math.max(0d, Math.min(1d, Math.min(value, mPercentMaxValue)));
        invalidate();
    }

    /**
     * 设置最大值的百分比值
     */
    public void setPercentMaxValue(double value) {
        mPercentMaxValue = Math.max(0d, Math.min(1d, Math.max(value, mPercentMinValue)));
        invalidate();
    }

    /**
     * 进度值，从百分比到绝对值
     * @return
     */
    @SuppressWarnings("unchecked")
    private float percentToAbsoluteValue(double normalized) {
        return (float) (mAbsoluteMinValue + normalized * (mAbsoluteMaxValue - mAbsoluteMinValue));
    }

    /**
     * 进度值，从绝对值到百分比
     */
    private double absoluteValueToPercent(float value) {
        if (0 == mAbsoluteMaxValue - mAbsoluteMinValue) {
            // prevent division by zero, simply return 0.
            return 0d;
        }
        return (value - mAbsoluteMinValue) / (mAbsoluteMaxValue - mAbsoluteMinValue);
    }

    /**
     * 进度值，从百分比值转换到屏幕中坐标值
     *
     */
    private float percentToScreen(double percentValue) {
        return (float) (mWidthPadding + percentValue * (getWidth() - 2 * mWidthPadding));
    }

    /**
     * 进度值，转换屏幕像素值到百分比值
     */
    private double screenToPercent(float screenCoord) {
        int width = getWidth();
        if (width <= 2 * mWidthPadding) {
            // prevent division by zero, simply return 0.
            return 0d;
        } else {
            double result = (screenCoord - mWidthPadding) / (width - 2 * mWidthPadding);
            return Math.min(1d, Math.max(0d, result));
        }
    }

    /**
     * Thumb枚举， 最大或最小
     *
     */
    private enum Thumb {
        MIN, MAX
    }


    public void setThumbListener(ThumbListener mThumbListener) {
        this.mThumbListener = mThumbListener;
    }

    /**
     * 滑块事件
     *
     * @author zouyulong
     */
    public interface ThumbListener {
        void onClickMinThumb(Number max, Number min);

        void onClickMaxThumb();

        void onUpMinThumb(Number max, Number min);

        void onUpMaxThumb();

        void onMinMove(Number max, Number min);

        void onMaxMove(Number max, Number min);
    }

    private String getProgressStr(int progress){
        String progressStr;
        if(mProgressTextFormat==HINT_FORMAT_TIME){
            progressStr = formatSecondTime(progress);
        }
        else{
            progressStr = String.valueOf(progress);
        }
        return progressStr;
    }

    /**
     * 格式化毫秒->00:00
     */
    private static String formatSecondTime(int millisecond) {
        if (millisecond == 0) {
            return "00:00";
        }
        int second = millisecond / 1000;
        int m = second / 60;
        int s = second % 60;
        if (m >= 60) {
            int hour = m / 60;
            int minute = m % 60;
            return hour + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (s > 9 ? s : "0" + s);
        } else {
            return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
        }
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}
