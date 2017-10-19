package org.wbing.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by HuiGu on 14/11/11.
 */
public class MetaDataReader {


    public static String readStringMetaDataFromApplication(String key) {
        Context context = ApplicationContextGetter.instance().get();

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.get(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int readIntMetaDataFromApplication(String key) {
        Context context = ApplicationContextGetter.instance().get();

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getInt(key, Integer.MIN_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }
}
