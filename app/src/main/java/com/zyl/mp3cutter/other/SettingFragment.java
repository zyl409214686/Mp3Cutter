package com.zyl.mp3cutter.other;

import android.content.Context;
import android.view.View;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseFragment;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.base.IBaseView;
import com.zyl.mp3cutter.common.ui.view.CommonDialog;
import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.databinding.FragmentSettingBinding;
import com.zyl.mp3cutter.home.bean.MusicInfo;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FOLDER;


/**
 * Description: 设置页
 * Created by zouyulong on 2017/11/22.
 * Person in charge :  zouyulong
 */
public class SettingFragment extends BaseFragment <IBaseView, BasePresenter<IBaseView>, FragmentSettingBinding> implements View.OnClickListener {

    public SettingFragment() {
    }

    @Override
    protected void ComponentInject(AppComponent appComponent) {

    }

    @Override
    protected void init(View view) {
        mDataBinding.rlClearCache.setOnClickListener(this);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.fragment_setting;
    }

//    @Override
//    protected View initView(LayoutInflater inflater, ViewGroup container) {
//        FragmentSettingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
//        binding.rlClearCache.setOnClickListener(this);
//        return binding.getRoot();
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rl_clear_cache:
                new CommonDialog.Builder().setTitleStr(getResources().getString(R.string.dialog_title))
                        .setContext(getActivity()).setContentStr(getResources().getString(R.string.setting_prompt_clearcache))
                        .setOnDialogListener(new CommonDialog.OnDialogClickListener() {
                            @Override
                            public void doOk() {
                                clearCache();
                            }
                        })
                        .build().show();
                break;
        }
    }

    private void clearCache(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                MyApplication.getInstances().getBoxStore().boxFor(MusicInfo.class).removeAll();
                FileUtils.delAllFile(RING_FOLDER);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
