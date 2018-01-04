package com.zyl.mp3cutter.other;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseFragment;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.base.IBaseView;
import com.zyl.mp3cutter.common.utils.AppContextUtils;
import com.zyl.mp3cutter.databinding.FragmentAboutBinding;


/**
 * Description: 关于页
 * Created by zouyulong on 2017/11/22.
 * Person in charge :  zouyulong
 */
public class AboutFragment extends BaseFragment<IBaseView, BasePresenter<IBaseView>, FragmentAboutBinding>{

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void ComponentInject(AppComponent appComponent) {

    }

    @Override
    protected void init(View view) {
        PackageInfo packageInfo = AppContextUtils.getPackageInfo(getActivity());
        mDataBinding.tvVersion.setText(String.format(
                getResources().getString(R.string.about_version), packageInfo.versionName,
                String.valueOf(packageInfo.versionCode)));
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.fragment_about;
    }

//    @Override
//    protected View initView(LayoutInflater inflater, ViewGroup container) {
//        FragmentAboutBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
//        PackageInfo packageInfo = AppContextUtils.getPackageInfo(getActivity());
//        binding.tvVersion.setText(String.format(
//                getResources().getString(R.string.about_version), packageInfo.versionName,
//                String.valueOf(packageInfo.versionCode)));
//        return binding.getRoot();
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
