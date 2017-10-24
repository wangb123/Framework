package org.wbing.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.wbing.base.WApp;
import org.wbing.base.utils.LogUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 王冰 on 2016/12/20.
 */

public class NetConfig implements Interceptor, CookieJar {
    private static NetConfig instance;

    private OkHttpClient client;
    private PersistentCookieStore cookieStore;
    private Gson gson;

    private NetConfig() {
    }

    public static NetConfig getInstance() {
        if (instance == null) {
            synchronized (NetConfig.class) {
                instance = new NetConfig();
            }
        }
        return instance;
    }

    public OkHttpClient getClient() {
        if (client == null)
            client = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(this)
                    .cookieJar(this)
                    .build();
        return client;
    }

    public Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder()
                    .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                    .create();
        return gson;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Request.Builder builder = request.newBuilder();
        Map<String, String> headers = WApp.getInstance().getHttpHeaders();
        if (headers != null) {
            LogUtils.e(headers.toString());
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Response response = chain.proceed(builder
                .build());
        LogUtils.i("request:" + request.toString());
        long t2 = System.nanoTime();
        okhttp3.MediaType mediaType = response.body().contentType();
        if ("text/html; charset=UTF-8".equals(mediaType.toString())||"application/json".equals(mediaType.toString())) {
            LogUtils.i(String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            String content = response.body().string();
            LogUtils.i("response body:" + content);
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
        return response;

    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                getCookieStore().add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return getCookieStore().get(url);
    }

    private PersistentCookieStore getCookieStore() {
        if (cookieStore == null)
            cookieStore = new PersistentCookieStore(WApp.getInstance().getApplicationContext());
        return cookieStore;
    }
}
