package org.wbing.base.utils;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import java.lang.reflect.Field;


/**
 * 获取屏幕,分辨率相关
 *
 * @author 6a209
 *         <p/>
 *         2011-12-14 下午10:28:41
 */
public class ScreenTools {
    private Context mCtx;
    private static ScreenTools mScreenTools;

    @Deprecated
    public static ScreenTools instance(Context ctx) {
        return instance();
    }

    public static ScreenTools instance() {
        if (null == mScreenTools) {
            mScreenTools = new ScreenTools();
        }
        return mScreenTools;
    }

    private ScreenTools() {
        mCtx = ApplicationContextGetter.instance().get();
    }

    public int getScreenWidth() {
        return mCtx.getResources().getDisplayMetrics().widthPixels;
    }

    @Deprecated
    public int dip2px(int dip) {
        return dip2px((float) dip);
    }

    public int dip2px(float dip) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip, mCtx.getResources().getDisplayMetrics()
        );
    }

    public int px2dip(int px) {
        float density = getDensity(mCtx);
        return Math.round(px / density);
    }

    public int getScreenDensityDpi() {
        return mCtx.getResources().getDisplayMetrics().densityDpi;
    }

    public float getXdpi() {
        return mCtx.getResources().getDisplayMetrics().xdpi;
    }

    public float getYdpi() {
        return mCtx.getResources().getDisplayMetrics().ydpi;
    }

    public float getDensity() {
        return mCtx.getResources().getDisplayMetrics().density;
    }

    public float getDensity(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    /**
     * ５40 的分辨率上是85 （
     *
     * @return
     */
    public int getScal() {
        return (int) (getScreenWidth() * 100 / 480);
    }

    /**
     * 宽全屏, 根据当前分辨率　动态获取高度
     * height 在８００*４８０情况下　的高度
     *
     * @return
     */
    public int get480Height(int height480) {
        int width = getScreenWidth();
        return (height480 * width / 480);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mCtx.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public int getScreenHeight() {
        return mCtx.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Get boundary of view which current page shows in.
     *
     * @param activity
     * @return index 0: width index 1:height
     */
    public int[] getContentBoundary(Activity activity) {
        int[] boundary = new int[2];
        if (activity == null) {
            return boundary;
        }

        View contentView = activity.findViewById(android.R.id.content);
        boundary[0] = contentView.getMeasuredWidth();
        boundary[1] = contentView.getMeasuredHeight();

        return boundary;
    }
}