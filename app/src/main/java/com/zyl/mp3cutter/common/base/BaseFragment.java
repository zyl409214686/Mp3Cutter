package com.zyl.mp3cutter.common.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.app.di.AppComponent;

import javax.inject.Inject;

/**
 * MVPPlugin
 */

public abstract class BaseFragment<V extends IBaseView,T extends BasePresenter<V>,
            B extends ViewDataBinding> extends Fragment implements IBaseView {
    protected MyApplication myApplication;

    @Inject
    public T mPresenter;

    protected  B mDataBinding;

    protected MyLoadingDialog mLoadingDialog;

    private static final String TAG_LOADING_DIALOG = "loadingdialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myApplication = (MyApplication)getActivity().getApplication();
        ComponentInject(myApplication.getAppComponent());//依赖注入
        mDataBinding = DataBindingUtil.inflate(inflater, initLayoutResId(), container, false);
        View view = mDataBinding.getRoot();
        init(view);
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

    protected abstract void init(View view);

    protected abstract int initLayoutResId();

    public void showLoadingDialogForFragment(Fragment fragment) {
        show(fragment.getFragmentManager());

    }

    public void showLoadingDialogForActicity(FragmentActivity activity) {
        show(activity.getSupportFragmentManager());
    }

    private void show(FragmentManager fm){
        if (mLoadingDialog == null) {
            mLoadingDialog = new MyLoadingDialog();
        } else if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show(fm, TAG_LOADING_DIALOG);
    }

    public void dismissLoadingDelay(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog!=null ){//&& mLoadingDialog.isShowing()
                    mLoadingDialog.dismiss();
                }
            }
        }, 1000);

    }

    public void dismissLoading() {
        if (mLoadingDialog != null) {//&& mLoadingDialog.isShowing()
            mLoadingDialog.dismiss();
        }
    }

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
