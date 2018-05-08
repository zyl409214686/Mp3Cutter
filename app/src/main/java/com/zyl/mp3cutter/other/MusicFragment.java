package com.zyl.mp3cutter.other;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.MyApplication;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseFragment;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.base.IBaseView;
import com.zyl.mp3cutter.databinding.FragmentMusicBinding;
import com.zyl.mp3cutter.music.bean.RecommendCollectionData;
import com.zyl.mp3cutter.music.bean.RecommendListNewAlbumInfo;
import com.zyl.mp3cutter.music.bean.RecommendListRadioInfo;
import com.zyl.mp3cutter.music.bean.RecommendListRecommendInfo;
import com.zyl.mp3cutter.music.network.CommonInterceptor;
import com.zyl.mp3cutter.music.network.IMusicService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * Description: 音乐
 * Created by zouyulong on 2017/11/22.
 * Person in charge :  zouyulong
 */
public class MusicFragment extends BaseFragment<IBaseView, BasePresenter<IBaseView>, FragmentMusicBinding> {
    private static final String HOME_DATA_URL = "http://tingapi.ting.baidu.com/v1/";
    private CommonAdapter mRecommendAdapter;
    private CommonAdapter mNewAdapter;
    private CommonAdapter mRadioAdapter;
    ArrayList<RecommendListRecommendInfo> mRecomendList = new ArrayList();
    ArrayList<RecommendListNewAlbumInfo> mNewList = new ArrayList();
    ArrayList<RecommendListRadioInfo> mRadioList = new ArrayList();

    public MusicFragment() {
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
        initRecommendAdapter();
        initNewAdapter();
        initRadioAdapter();
        requestData();
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.fragment_music;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initRecommendAdapter(){
        mRecommendAdapter = new CommonAdapter<RecommendListRecommendInfo>(getActivity(), R.layout.item_recommend_playlist, mRecomendList) {

            @Override
            protected void convert(ViewHolder holder, final RecommendListRecommendInfo musicInfo, int position) {
                holder.setText(R.id.playlist_name, musicInfo.getTitle());
                holder.setText(R.id.playlist_listen_count, "1000");
                if(TextUtils.isEmpty(musicInfo.getPic())){
                    holder.setImageDrawable(R.id.playlist_art, getResources().getDrawable(R.mipmap.music_icon));
                }
                else {
                    RequestOptions options = new RequestOptions().placeholder(R.mipmap.music_icon);
                    Glide.with(getActivity()).load(musicInfo.getPic())
                            .apply(options).into((ImageView) holder.getView(R.id.playlist_art));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     }
                });
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDataBinding.rlRecommend.setLayoutManager(gridLayoutManager);
        mDataBinding.rlRecommend.setAdapter(mRecommendAdapter);
        mDataBinding.rlRecommend.setHasFixedSize(true);
//        mDataBinding.rlRecommend.setNestedScrollingEnabled(false);
    }

    private void initNewAdapter(){
        mNewAdapter = new CommonAdapter<RecommendListNewAlbumInfo>(getActivity(), R.layout.item_recommend_playlist, mNewList) {

            @Override
            protected void convert(ViewHolder holder, final RecommendListNewAlbumInfo musicInfo, int position) {
                holder.setText(R.id.playlist_name, musicInfo.getTitle());
                holder.setText(R.id.playlist_listen_count, "1000");
                if(TextUtils.isEmpty(musicInfo.getPic())){
                    holder.setImageDrawable(R.id.playlist_art, getResources().getDrawable(R.mipmap.music_icon));
                }
                else {
                    RequestOptions options = new RequestOptions().placeholder(R.mipmap.music_icon);
                    Glide.with(getActivity()).load(musicInfo.getPic())
                            .apply(options).into((ImageView) holder.getView(R.id.playlist_art));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDataBinding.rlNew.setLayoutManager(gridLayoutManager);
        mDataBinding.rlNew.setAdapter(mNewAdapter);
        mDataBinding.rlNew.setHasFixedSize(true);
//        mDataBinding.rlNew.setNestedScrollingEnabled(false);

    }

    private void initRadioAdapter(){
        mRadioAdapter = new CommonAdapter<RecommendListRadioInfo>(getActivity(), R.layout.item_recommend_playlist, mRadioList) {

            @Override
            protected void convert(ViewHolder holder, final RecommendListRadioInfo musicInfo, int position) {
                holder.setText(R.id.playlist_name, musicInfo.getTitle());
                holder.setText(R.id.playlist_listen_count, "1000");
                if(TextUtils.isEmpty(musicInfo.getPic())){
                    holder.setImageDrawable(R.id.playlist_art, getResources().getDrawable(R.mipmap.music_icon));
                }
                else {
                    RequestOptions options = new RequestOptions().placeholder(R.mipmap.music_icon);
                    Glide.with(getActivity()).load(musicInfo.getPic())
                            .apply(options).into((ImageView) holder.getView(R.id.playlist_art));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDataBinding.rlRadio.setLayoutManager(gridLayoutManager);
        mDataBinding.rlRadio.setAdapter(mRadioAdapter);
        mDataBinding.rlRadio.setHasFixedSize(true);
//        mDataBinding.rlRadio.setNestedScrollingEnabled(false);

    }

    public void requestData() {
        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                CommonInterceptor commonInterceptor = new CommonInterceptor();
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(commonInterceptor)
                        .addInterceptor(logging)
                        .build();

                Retrofit retrofit = new Retrofit.Builder().baseUrl(HOME_DATA_URL).client(client)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                                addConverterFactory(new Converter.Factory() {
                                    @Override
                                    public Converter<ResponseBody, RecommendCollectionData> responseBodyConverter(Type type, Annotation[] annotations,
                                                                                            Retrofit retrofit) {
                                        return new Converter<ResponseBody, RecommendCollectionData>() {
                                            @Override
                                            public RecommendCollectionData convert(ResponseBody value) throws IOException {
                                                return parseData(value.string());
                                            }
                                        };
                                    }
                                }).build();
                IMusicService musicService = retrofit.create(IMusicService.class);
                musicService.getHomeDatas().subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RecommendCollectionData>() {
                    @Override
                    public void accept(RecommendCollectionData s) throws Exception {
//                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
//                        Logger.d(s);
                        mRecomendList.clear();
                        mRecomendList.addAll(s.getRecomendList());
                        mRecommendAdapter.notifyDataSetChanged();
                        mNewList.clear();
                        mNewList.addAll(s.getNewAlbumsList());
                        mNewAdapter.notifyDataSetChanged();
                        mRadioList.clear();
                        mRadioList.addAll(s.getRadioList());
                        mRadioAdapter.notifyDataSetChanged();
//                        mDataBinding.
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        Logger.d("error:" + throwable.getMessage());
                    }
                });
                return null;
            }
        }.execute();

    }


    private RecommendCollectionData parseData(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(jsonStr);
        JsonObject object = el.getAsJsonObject().get("result").getAsJsonObject();
        JsonArray radioArray = object.get("radio").getAsJsonObject().get("result").getAsJsonArray();
        JsonArray recommendArray = object.get("diy").getAsJsonObject().get("result").getAsJsonArray();
        JsonArray newAlbumArray = object.get("mix_1").getAsJsonObject().get("result").getAsJsonArray();
        RecommendCollectionData data = new RecommendCollectionData();
        ArrayList<RecommendListNewAlbumInfo> newAlbumsList = new ArrayList();
        ArrayList<RecommendListRadioInfo> radioList = new ArrayList();
        ArrayList<RecommendListRecommendInfo> recommendList = new ArrayList();
        for (int i = 0; i < 6; i++) {
            recommendList.add(MyApplication.gsonInstance().fromJson(recommendArray.get(i), RecommendListRecommendInfo.class));
            newAlbumsList.add(MyApplication.gsonInstance().fromJson(newAlbumArray.get(i), RecommendListNewAlbumInfo.class));
            radioList.add(MyApplication.gsonInstance().fromJson(radioArray.get(i), RecommendListRadioInfo.class));
        }
        data.setNewAlbumsList(newAlbumsList);
        data.setRadioList(radioList);
        data.setRecomendList(recommendList);
        return data;
    }
}
