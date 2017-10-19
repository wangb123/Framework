package org.wbing.base.utils.toast;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by xiaochuang on 09/11/2016.
 */

public class SimpleMLToastQueue implements MLToastQueue {

    private static SimpleMLToastQueue sInstance = new SimpleMLToastQueue();
    private final LinkedBlockingDeque<MLToast> toastQueue;
    private boolean toasting = false;

    private SimpleMLToastQueue() {
        toastQueue = new LinkedBlockingDeque<>();
    }

    public static SimpleMLToastQueue getInstance() {
        return sInstance;
    }

    @Override
    public synchronized void enqueue(MLToast toast) {
        if (!toasting) {
            toasting = true;
            toast.show(showNextWhenFinish);
        } else {
            toastQueue.offer(toast);
        }
    }

    private final ToastCallback showNextWhenFinish = new ToastCallback() {
        @Override
        public void onToastFinished() {
            showNextIfHas();
        }
    };

    private synchronized void showNextIfHas() {
        MLToast toast = toastQueue.poll();
        if (toast == null) {
            toasting = false;
            return;
        }

        toast.show(showNextWhenFinish);
    }

}
