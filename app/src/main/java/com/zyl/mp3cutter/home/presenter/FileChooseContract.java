package com.zyl.mp3cutter.home.presenter;

import com.zyl.mp3cutter.common.base.IBasePresenter;
import com.zyl.mp3cutter.common.base.IBaseView;
import com.zyl.mp3cutter.home.bean.MusicInfo;

import java.util.List;

/**
 * Description: file choose contract契约类
 * Created by zouyulong on 2018/2/2.
 * Person in charge :  zouyulong
 */

public class FileChooseContract {
    public interface View extends IBaseView {
        void getMusicList(List<MusicInfo> musiclist);
    }


    public interface Presenter extends IBasePresenter {
        void loadFile(final boolean isforceScan);
    }
}
