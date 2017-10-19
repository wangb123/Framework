package org.wbing.base.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 6a209 on 15/8/24.
 */
public class ProcessForegroundUtils {

//    ActivityManager.RunningAppProcessInfo mProcessInfo;

    static ProcessForegroundUtils sProcessInfo = new ProcessForegroundUtils();

    public static ProcessForegroundUtils instance(){
        return sProcessInfo;
    }

    @Deprecated
    public boolean isForeground(Context context) {
        return isForeground();
    }

    public boolean isForeground() {
        Context context = ApplicationContextGetter.instance().get();

//        if(null != mProcessInfo){
//            return mProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
//        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
