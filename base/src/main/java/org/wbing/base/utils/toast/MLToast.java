package org.wbing.base.utils.toast;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by xiaochuang on 09/11/2016.
 * Note: ML Stands for MeiLi-inc, not for...you know...
 */
public class MLToast {
    public static final int DISMISS_ANIMATION_DURATION = 1000;
    public static final int SHOW_ANIMATION_DURATION = 300;
    public static final int TOAST_DURATION_LONG = 5000;
    public static final int TOAST_DURATION_SHORT = 2000;

    private final WeakReference<Activity> activityRef;
    private final String message;
    private final int length;
    private Handler mHandler;

    public MLToast(Activity activity, String message, int length) {
        this.activityRef = new WeakReference<>(activity);
        this.message = message;
        this.length = length;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void show(final ToastCallback callback) {
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) {
            callback.onToastFinished();
            return;
        }

        final View toastView = ToastViewFactories.defaultFactory().create(activity, message);
        ViewGroup.LayoutParams params = toastView.getLayoutParams();
        if (params == null) params = getDefaultLayoutParams();
        activity.addContentView(toastView, params);

        toastView.setAlpha(0);
        toastView.animate().alpha(1).setDuration(SHOW_ANIMATION_DURATION).start();

        long toastDuration = length == Toast.LENGTH_LONG ? TOAST_DURATION_LONG : TOAST_DURATION_SHORT;
        mHandler.postDelayed(new RemoveToastRunnable(toastView, callback), toastDuration);
    }

    private static class RemoveToastRunnable implements Runnable {
        private final WeakReference<View> viewRef;
        private ToastCallback callback;

        RemoveToastRunnable(View view, ToastCallback callback) {
            this.viewRef = new WeakReference<View>(view);
            this.callback = callback;
        }

        @Override
        public void run() {
            final View toastView = viewRef.get();
            if (toastView==null) {
                callback.onToastFinished();
                return;
            }

            toastView.clearAnimation();
            toastView.animate()
                    .alpha(0)
                    .setDuration(DISMISS_ANIMATION_DURATION)
                    .setListener(new AnimatorEndListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (toastView!=null && toastView.getParent()!=null) {
                                ((ViewGroup) toastView.getParent()).removeView(toastView);
                            }
                            callback.onToastFinished();
                        }
                    })
                    .start();
        }
    }

    private static FrameLayout.LayoutParams getDefaultLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

}
