package com.zyl.mp3cutter.common.base;

/**
 * MVPPlugin
 */

public class BasePresenter<V extends IBaseView> implements IBasePresenter {
    protected V mView;

    public BasePresenter(V rootView) {
        this.mView = rootView;
    }
}
