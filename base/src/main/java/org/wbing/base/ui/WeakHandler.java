package org.wbing.base.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by yizheng on 16/3/14.
 */
public class WeakHandler extends Handler {
    private WeakReference<Callback> mCallbackWeak;

    public WeakHandler(Callback callback) {
        this.mCallbackWeak = new WeakReference(callback);
    }

    public WeakHandler(Looper looper, Callback callback) {
        super(looper);
        this.mCallbackWeak = new WeakReference(callback);
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Callback callback = (Callback)this.mCallbackWeak.get();
        if(callback != null) {
            callback.handleMessage(msg);
        }

    }
}
