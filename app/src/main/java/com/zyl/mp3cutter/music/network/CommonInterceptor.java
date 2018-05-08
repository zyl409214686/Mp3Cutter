package com.zyl.mp3cutter.music.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * 封装公共参数（Key和密码）
 * <p>
 */

public class CommonInterceptor implements Interceptor {
    private static final String PARAM_FROM = "from";
    private static final String PARAM_VERSION = "version";
    private static final String PARAM_CHANNEL = "channel";
    private static final String PARAM_OPERATOR = "operator";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_CUID = "cuid";

    public CommonInterceptor() {
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request().newBuilder().removeHeader("User-Agent")
        .addHeader("User-Agent", System.getProperty("http.agent")).build();
        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter(PARAM_FROM, "android")
                .addQueryParameter(PARAM_VERSION, "5.8.1.0")
                .addQueryParameter(PARAM_CHANNEL, "ppzs")
                .addQueryParameter(PARAM_OPERATOR, "3")
                .addQueryParameter(PARAM_METHOD, "baidu.ting.plaza.index")
                .addQueryParameter(PARAM_CUID, "89CF1E1A06826F9AB95A34DC0F6AAA14");

        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .build();

        return chain.proceed(newRequest);
    }
    //?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza.index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14

}
