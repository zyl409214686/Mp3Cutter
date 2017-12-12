package com.zyl.mp3cutter.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.app.di.AppComponent;

import javax.inject.Inject;

/**
 * MVPPlugin
 */

public abstract class BaseFragment<V extends IBaseView,T extends BasePresenter<V>> extends Fragment implements IBaseView {
    protected MyApplication myApplication;

    @Inject
    public T mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myApplication = (MyApplication)getActivity().getApplication();
        ComponentInject(myApplication.getAppComponent());//依赖注入
        View view = initView(inflater, container);
        return view;
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    /**
     * 依赖注入的入口
     */
    protected abstract void ComponentInject(AppComponent appComponent);

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

//    public  <T> T getInstance(Object o, int i) {
//            try {
//                return ((Class<T>) ((ParameterizedType) (o.getClass()
//                        .getGenericSuperclass())).getActualTypeArguments()[i])
//                        .newInstance();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (ClassCastException e) {
//                e.printStackTrace();
//            } catch (java.lang.InstantiationException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
}
