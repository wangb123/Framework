package org.wbing.base.utils.toast;

import android.app.Activity;
import android.view.View;

/**
 * Created by xiaochuang on 10/11/2016.
 */

public interface ToastViewFactory {
    /**
     * Create a View that will be used in Toast.
     * This View will be added to a FrameLayout. Call {@link View#setLayoutParams} on the View if you want to control its layout.
     * @param activity
     * @param msg
     * @return A view that will be used in Toast
     */
    View create(Activity activity, String msg);
}
