package com.yaolan.common;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 王冰 on 2017/8/17.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("MobileAppNew/{path}")
    Observable<HttpModel<Map<String, String>>> getHttpModel(@Path("path") String path, @FieldMap Map<String, String> params);

    @POST("MobileAppNew/{path}")
    Observable<HttpModel<Map<String, String>>> getHttpModel(@Path("path") String path);

    @FormUrlEncoded
    @POST("MobileAppNew/{path}")
    Observable<HttpModel<List<Map<String, String>>>> getHttpModelList(@Path("path") String path, @FieldMap Map<String, String> params);

    @POST("MobileAppNew/{path}")
    Observable<HttpModel<List<Map<String, String>>>> getHttpModelList(@Path("path") String path);

    @FormUrlEncoded
    @POST("MobileAppNew/{path}")
    Observable<HttpModel<String>> getStringModel(@Path("path") String path, @FieldMap Map<String, String> params);

    @POST("MobileAppNew/{path}")
    Observable<HttpModel<String>> getStringModel(@Path("path") String path);

    @POST("MobileAppNew/{path}")
    Observable<HttpModel<JsonObject>> getJsonObject(@Path("path") String path);

    @FormUrlEncoded
    @POST("MobileAppNew/{path}")
    Observable<HttpModel<JsonObject>> getJsonObject(@Path("path") String path, @FieldMap Map<String, String> params);

}
