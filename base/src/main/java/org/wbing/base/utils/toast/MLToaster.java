package org.wbing.base.utils.toast;

import android.app.Activity;

/**
 * Created by xiaochuang on 09/11/2016.
 */

public class MLToaster implements Toaster {
    private final MLToastQueue queue;
    public MLToaster(MLToastQueue queue) {
        this.queue = queue;
    }

    @Override
    public void toast(Activity activity, String msg, int length) {
        queue.enqueue(new MLToast(activity, msg, length));
    }

    private static MLToaster singleton;
    public static MLToaster getInstance() {
        if(singleton == null) {
            synchronized(MLToaster.class) {
                if(singleton == null) {
                    singleton = new MLToaster(SimpleMLToastQueue.getInstance());
                }
            }
        }
        return singleton;
    }


}
