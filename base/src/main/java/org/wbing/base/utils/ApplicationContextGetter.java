package org.wbing.base.utils;

import android.app.Application;

/**
 * 对外提供当前Application，可以直接使用在只需要Application Context的地方
 * Created by dolphinWang on 15/9/24.
 */
public class ApplicationContextGetter {

    private static volatile ApplicationContextGetter sInstance;

    private static Application sApplication;

    public static synchronized ApplicationContextGetter instance() {
        if (sInstance == null) {
            sInstance = new ApplicationContextGetter();
        }

        return sInstance;
    }

    public void setApplicationContext(Application application) {
        sApplication = application;
    }

    public Application get() {
        return sApplication;
    }
}
