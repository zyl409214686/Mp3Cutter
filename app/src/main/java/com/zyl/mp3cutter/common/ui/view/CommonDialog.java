package com.zyl.mp3cutter.common.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zyl.mp3cutter.R;

/**
 * Description: 通用dialog
 * Created by zouyulong on 2017/11/13.
 * Person in charge :  zouyulong
 */

public class CommonDialog extends Dialog {
    private TextView mTitleTv;
    private TextView mContentTv;
    private Context mContext;
    public TextView mConfirmTv;
    public TextView mCancelTv;
    private EditText mInputEt;
    private String mContentStr;
    private String mTitleStr;
    private String mConfirmStr;
    private String mCancelStr;
    private int mRightTextColor;
    private boolean mIsShowOne;
    private boolean mIsShowInput;
    private String mHintContent;
    private boolean mIsPasswordInput;
    private int mCancelColor;// = Color.parseColor("#009688");
    public OnDialogClickListener mOnClickListener;

    private CommonDialog(Builder builder) {
        super(builder.context, R.style.dialog_normal);
        mContext = builder.context;
        setContentView(R.layout.dialog_common);
        initView();
        initForBuilder(builder);
        refreshView();
    }

    private void initForBuilder(Builder builder){
        mTitleStr = builder.titleStr;
        mContentStr = builder.contentStr;
        mConfirmStr = builder.confirmStr;
        mCancelStr= builder.cancelStr;
        mOnClickListener = builder.mOnDialogClickListener;
        mCancelColor = builder.cancelColor;
        mRightTextColor = builder.rightTextColor;
        mIsShowOne = builder.isShowOne;
        mIsShowInput = builder.isShowInput;
        mHintContent = builder.hintContent;
        mIsPasswordInput = builder.isPasswordInput;
        setCancelable(builder.canCancel);
    }

    private void initView() {
        this.mTitleTv = (TextView) this.findViewById(R.id.tv_dialog_title);
        this.mContentTv = (TextView) this.findViewById(R.id.tv_dialog_content);
        this.mConfirmTv = (TextView) this.findViewById(R.id.tv_dialog_confirm);
        this.mCancelTv = (TextView) this.findViewById(R.id.tv_dialog_cancle);
        this.mInputEt = (EditText) this.findViewById(R.id.et_dialog_input);
    }

//    public CommonDialog(Context context, boolean showtwo, String message, int btnRightTextColor, int btnLeftTextColor, OnDialogClickListener listener) {
//        super(context, R.style.dialog_normal);
//        this.createDialog(context, false, showtwo, (String) null, message, false, (String) null, btnRightTextColor, btnLeftTextColor, listener);
//    }
//
//    public CommonDialog(Context context, boolean showtwo, String name, String message, int btnRightTextColor, int btnLeftTextColor, OnDialogClickListener listener) {
//        super(context, R.style.dialog_normal);
//        this.createDialog(context, false, showtwo, name, message, false, (String) null, btnRightTextColor, btnLeftTextColor, listener);
//    }
//
//    public CommonDialog(Context context, boolean passwordInput, boolean showtwo, String name, String message, boolean isShowEt, String hintContent, int btnRightTextColor, int btnLeftTextColor, OnDialogClickListener listener) {
//        super(context, R.style.dialog_normal);
//        this.createDialog(context, passwordInput, showtwo, name, message, isShowEt, hintContent, btnRightTextColor, btnLeftTextColor, listener);
//    }

    private void refreshView(){
        if (mTitleStr != null) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(mTitleStr);
        }
        else{
            mTitleTv.setVisibility(View.GONE);
        }
        if (mContentStr != null) {
            this.mContentTv.setVisibility(View.VISIBLE);
            this.mContentTv.setText(mContentStr);
        }
        else{
            mContentTv.setVisibility(View.GONE);
        }
        if (mRightTextColor != 0) {
            this.mConfirmTv.setTextColor(mRightTextColor);
        }
        if(mCancelColor!=0){
            this.mCancelTv.setTextColor(this.mCancelColor);
        }
        if (mIsShowOne) {
            this.mCancelTv.setVisibility(View.GONE);
        }
        else{
            this.mCancelTv.setVisibility(View.VISIBLE);
        }
        if (mIsShowInput) {
            this.mInputEt.setVisibility(View.VISIBLE);
            this.mInputEt.setHint(mHintContent);
            this.mInputEt.setInputType(mIsPasswordInput ? InputType.TYPE_TEXT_VARIATION_PASSWORD :
                    InputType.TYPE_CLASS_TEXT);
        }
        else{
            this.mInputEt.setVisibility(View.GONE);
        }
        this.setDataAndListener();
    }

    private void setDataAndListener() {
        this.mConfirmTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommonDialog.this.close();
                if (CommonDialog.this.mOnClickListener != null) {
                    if(TextUtils.isEmpty(CommonDialog.this.getEditText()))
                        (CommonDialog.this.mOnClickListener).doOk();
                    else{
                        (CommonDialog.this.mOnClickListener).doOk(CommonDialog.this.getEditText());
                    }
                }

            }
        });
        this.mCancelTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommonDialog.this.close();
                if (CommonDialog.this.mOnClickListener != null) {
                    CommonDialog.this.mOnClickListener.doCancle();
                }

            }
        });
    }

    public void close() {
        if (!((Activity) this.mContext).isFinishing()) {
            ((Activity) this.mContext).runOnUiThread(new Runnable() {
                public void run() {
                    if (CommonDialog.this.isShowing()) {
                        CommonDialog.this.dismiss();
                    }

                }
            });
        }

    }

    public String getEditText() {
        return this.mInputEt.getText().toString().trim();
    }

    public static class OnDialogClickListener {
        public void doOk() {
        }

        public void doOk(String text) {
        }

        public void doCancle() {
        }
    }


    public static final class Builder {
        private Context context;
        private String titleStr;
        private String contentStr;
        private int cancelColor;
        private String confirmStr;
        private String cancelStr;
        private int rightTextColor;
        private boolean isShowOne;
        private boolean isShowInput;
        private String hintContent;
        private boolean isPasswordInput;
        private boolean canCancel = true;
        private OnDialogClickListener mOnDialogClickListener;

        public Builder() {
        }

        public Builder setTitleStr(String val) {
            titleStr = val;
            return this;
        }

        public Builder setContext(Context context){
            this.context = context;
            return this;
        }

        public Builder setContentStr(String val) {
            contentStr = val;
            return this;
        }

        public Builder setConfirmStr(String val) {
            confirmStr = val;
            return this;
        }

        public Builder setCancelStr(String val) {
            cancelStr = val;
            return this;
        }

        public Builder setCancelColor(int val) {
            cancelColor = val;
            return this;
        }

        public Builder setRightTextColor(int val) {
            rightTextColor = val;
            return this;
        }

        public Builder setIsShowOne(boolean val) {
            isShowOne = val;
            return this;
        }

        public Builder setIsShowInput(boolean val) {
            isShowInput = val;
            return this;
        }

        public Builder setHintContent(String val) {
            hintContent = val;
            return this;
        }

        public Builder setPasswordInput(boolean val) {
            isPasswordInput = val;
            return this;
        }

        public Builder setOnDialogListener(OnDialogClickListener val) {
            mOnDialogClickListener = val;
            return this;
        }

        public CommonDialog build() {
            return new CommonDialog(this);
        }

        public boolean isCanCancel() {
            return canCancel;
        }

        public Builder setCanCancel(boolean canCancel) {
            this.canCancel = canCancel;
            return this;
        }
    }
}