package org.wbing.framework;

import android.app.Activity;
import android.os.Bundle;

import org.wbing.base.WApp;
import org.wbing.base.utils.LogUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by 10213 on 2017/10/25.
 */

public class MyApp extends WApp {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                LogUtils.e(activity.getClass().getSimpleName());

            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.e(activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.e(activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.e(activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.e(activity.getClass().getSimpleName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                LogUtils.e(activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.e(activity.getClass().getSimpleName());
            }
        });
    }

    @Override
    public File getRootFile() {
        return null;
    }

    @Override
    public Map<String, String> getHttpHeaders() {
        return null;
    }

    @Override
    public void initializeAllProcessRouter() {

    }

    @Override
    protected void initializeLogic() {

    }

    @Override
    public boolean needMultipleProcess() {
        return false;
    }
}
