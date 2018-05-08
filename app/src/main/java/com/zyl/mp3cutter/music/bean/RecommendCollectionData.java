package com.zyl.mp3cutter.music.bean;

import java.util.ArrayList;

/**
 * Description: com.zyl.mp3cutter.music.bean
 * Created by zouyulong on 2018/3/28.
 */

public class RecommendCollectionData {
    private ArrayList<RecommendListRecommendInfo> mRecomendList;
    private ArrayList<RecommendListNewAlbumInfo> mNewAlbumsList;
    private ArrayList<RecommendListRadioInfo> mRadioList ;

    public ArrayList<RecommendListRecommendInfo> getRecomendList() {
        return mRecomendList;
    }

    public RecommendCollectionData setRecomendList(ArrayList<RecommendListRecommendInfo> mRecomendList) {
        this.mRecomendList = mRecomendList;
        return this;
    }

    public ArrayList<RecommendListNewAlbumInfo> getNewAlbumsList() {
        return mNewAlbumsList;
    }

    public RecommendCollectionData setNewAlbumsList(ArrayList<RecommendListNewAlbumInfo> mNewAlbumsList) {
        this.mNewAlbumsList = mNewAlbumsList;
        return this;
    }

    public ArrayList<RecommendListRadioInfo> getRadioList() {
        return mRadioList;
    }

    public RecommendCollectionData setRadioList(ArrayList<RecommendListRadioInfo> mRadioList) {
        this.mRadioList = mRadioList;
        return this;
    }


}
