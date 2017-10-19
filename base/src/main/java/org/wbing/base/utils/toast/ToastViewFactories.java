package org.wbing.base.utils.toast;

/**
 * Created by xiaochuang on 10/11/2016.
 */

public class ToastViewFactories {

    private static ToastViewFactory toastViewFactory;

    public static ToastViewFactory defaultFactory() {
        if (toastViewFactory == null) toastViewFactory = new PinkToastViewFactory();
        return toastViewFactory;
    }

    public static void setDefaultFactory(ToastViewFactory factory) {
        toastViewFactory = factory;
    }

}
