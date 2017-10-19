package org.wbing.base;

import android.app.Application;
import android.content.Context;

import org.wbing.base.utils.ApplicationContextGetter;
import org.wbing.base.utils.MetaDataReader;

import java.io.File;

/**
 * Created by 王冰 on 2017/8/14.
 */

public abstract class WApp extends Application {
    private static WApp instance;

    public static WApp getInstance() {
        return instance;
    }

    public String scheme = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ApplicationContextGetter.instance().setApplicationContext(this);
    }

    private void initAppScheme() {
        this.scheme = MetaDataReader.readStringMetaDataFromApplication("key_app_scheme");
    }

    public String getAppScheme() {
        return this.scheme;
    }

    public abstract File getRootFile();


}
