package org.wbing.base.utils.toast;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import org.wbing.base.view.toast.PinkToast;


/**
 * Created by xiaochuang on 10/11/2016.
 */

public class PinkToastViewFactory implements ToastViewFactory {
    @Override
    public View create(Activity activity, String msg) {
        return PinkToast.makeText(activity, msg, Toast.LENGTH_SHORT).getView();
    }

    public static PinkToastViewFactory get() {
        return new PinkToastViewFactory();
    }

}
