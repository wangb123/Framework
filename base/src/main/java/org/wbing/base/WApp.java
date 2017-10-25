package org.wbing.base;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mogujie.security.MGSoTool;
import com.spinytech.macore.MaApplication;

import org.wbing.base.utils.ApplicationContextGetter;
import org.wbing.base.utils.MetaDataReader;

import java.io.File;
import java.util.Map;

/**
 * Created by 王冰 on 2017/8/14.
 */

public abstract class WApp extends MaApplication {
    private static WApp instance;

    public static WApp getInstance() {
        return instance;
    }

    public String scheme = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MGSoTool.init(this);
        initFresco();
    }

    protected void initFresco() {
        Fresco.initialize(this);
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

    public abstract Map<String, String> getHttpHeaders();

}
