package com.zyl.mp3cutter.home.presenter;

import android.os.Environment;

import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.utils.Mp3ScanUtils;
import com.zyl.mp3cutter.home.bean.MusicInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//import com.zyl.mp3cutter.home.bean.MusicInfoDao;

/**
 * Description: filechoosepresenter
 * Created by zouyulong on 2018/2/2.
 * Person in charge :  zouyulong
 */
public class FileChoosePresenter extends BasePresenter<FileChooseContract.View> implements FileChooseContract.Presenter {
    private String mSdcardRootPath;
    Box<MusicInfo> mBox;
    @Inject
    public FileChoosePresenter(FileChooseContract.View view) {
        super(view);
        init();
    }

    private void init(){
        mBox = MyApplication.getInstances().getBoxStore().boxFor(MusicInfo.class);
        mSdcardRootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
    }


    /**
     * load 文件
     * @param isforceScan  true 强制重新扫描并更新数据库， 数据库有数据的话从数据库拿
     */
    public void loadFile(final boolean isforceScan){
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                List<MusicInfo> datas = mBox.getAll();
                if(datas==null||datas.size()<=0||isforceScan) {
                    datas = new ArrayList<>();
                }
                if(isforceScan) {
                    addDataToDB(datas);
                }
                else{
                    if(datas.size()<=0)
                        addDataToDB(datas);
                }
                e.onNext(datas);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<MusicInfo>>() {
                    @Override
                    public void accept(List<MusicInfo> datas) throws Exception {
                        if(mView!=null)
                            mView.getMusicList(datas);
                    }
                });
    }

    private void addDataToDB(List<MusicInfo> datas) throws Exception {
        mBox.removeAll();
        Mp3ScanUtils.scanMp3File(datas, mSdcardRootPath);
        if(datas!=null) {
            mBox.put(datas);
        }
    }
}
