package com.zyl.mp3cutter.music.network;

import com.zyl.mp3cutter.music.bean.RecommendCollectionData;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Description: com.zyl.mp3cutter.music.network
 * Created by zouyulong on 2018/3/25.
 */

public interface IMusicService {
    @GET("restserver/ting")
    Observable<RecommendCollectionData> getHomeDatas();
    //?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza.index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14
}
