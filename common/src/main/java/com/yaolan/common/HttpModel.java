package com.yaolan.common;

import com.yaolan.common.config.Api;

import org.wbing.base.config.NetConfig;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 王冰 on 2017/8/17.
 */

public class HttpModel<Data> {
    private int code;
    private String msg;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    private static ApiService apiService;

    public static ApiService get() {
        if (apiService == null) {
            create();
        }
        return apiService;
    }

    private static synchronized void create() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }

    }

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Api.Host)
                .client(NetConfig.getInstance().getClient())
                .addConverterFactory(GsonConverterFactory.create(NetConfig.getInstance().getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

}
