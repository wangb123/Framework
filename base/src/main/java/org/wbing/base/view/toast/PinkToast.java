/**
 * @author zhouzhengnan
 * @date 20 Oct 2013
 */
package org.wbing.base.view.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.wbing.base.R;
import org.wbing.base.utils.ProcessForegroundUtils;
import org.wbing.base.utils.RefInvoker;
import org.wbing.base.utils.ScreenTools;
import org.wbing.base.utils.toast.MLToaster;
import org.wbing.base.utils.toast.ToastViewFactories;
import org.wbing.base.utils.toast.ToastViewFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;


/**
 * @author zhouzhengnan
 * @date 20 Oct 2013
 */
public class PinkToast extends Toast {
    private boolean isShowing = false;
    private Toast toast;
    private static int mToastBg;

    private static boolean sNeedShow = true;

    /**
     * @param context
     */
    public PinkToast(Context context) {
        super(context);
    }

    private static PinkToast initPinkToast(Toast toast, Context context) {
        PinkToast pinkToast = new PinkToast(context);
        pinkToast.toast = toast;
        return pinkToast;
    }

    @Deprecated
    public static void setToastBg(int drawableId) {
        mToastBg = drawableId;
    }

    public static PinkToast makeTextWithRightIcon(Context context, CharSequence text, int duration) {
        return makeTextWithIcon(context, text, duration, R.mipmap.right);
    }

    public static PinkToast makeTextWithErrorIcon(Context context, CharSequence text, int duration) {
        return makeTextWithIcon(context, text, duration, R.mipmap.error);
    }

    public static PinkToast makeTextWithMarkIcon(Context context, CharSequence text, int duration) {
        return makeTextWithIcon(context, text, duration, R.mipmap.mark);
    }

    public static PinkToast makeTextWithIcon(Context context, CharSequence text, int duration, int resId) {
        return build(context, text, duration, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, resId);
    }

    @SuppressLint("ShowToast")
    public static PinkToast makeText(Context context, CharSequence text, int duration) {
        return makeTextWithIcon(context, text, duration, Integer.MIN_VALUE);
    }

    public static PinkToast makeTextInBackgroud(Context context, CharSequence text, int duration) {
        return build(context, text, duration, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, true);
    }

    @SuppressLint("ShowToast")
    public static PinkToast makeText(Context context, int id, int duration) {
        CharSequence cs = context.getResources().getString(id);
        return makeText(context, cs, duration);
    }

    /**
     * Set a default {@link ToastViewFactory} when showing toast using {@link #actToast(Activity, String, int)}
     * @param factory the {@link ToastViewFactory} that will be used to create toast view
     */
    public static void setActToastViewFactory(ToastViewFactory factory) {
        if (factory == null) return;
        ToastViewFactories.setDefaultFactory(factory);
    }

    /**
     * Show a toast that's bound to the given activity, when the activity exit, the toast will be gone.
     * @param activity the activity that this toast is bound to.
     * @param msg The message to show
     * @param length Use {@link Toast#LENGTH_SHORT}Toast.LENGTH_SHORT or {@link Toast#LENGTH_LONG}
     */
    public static void actToast(Activity activity, String msg, int length) {
        MLToaster.getInstance().toast(activity, msg, length);
    }

    /**
     * Same as {@link #actToast(Activity activity, String msg, int length)}, use {@link Toast#LENGTH_SHORT} as the length
     * @param activity
     * @param msg
     */
    public static void actToast(Activity activity, String msg) {
        actToast(activity, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Same as {@link #actToast(Activity activity, String msg, int length)}, use {@link Activity#getString} as the message
     * @param activity
     * @param msgId
     */
    public static void actToast(Activity activity, @StringRes int msgId, int length) {
        actToast(activity, activity.getString(msgId), length);
    }

    /**
     * Same as {@link #actToast(Activity activity, String msg, int length)}, use {@link Toast#LENGTH_SHORT} as the length
     * @param activity
     * @param msgId
     */
    public static void actToast(Activity activity, @StringRes int msgId) {
        actToast(activity, msgId, Toast.LENGTH_SHORT);
    }

    private static Toast genMGToast(Toast toast)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchFieldException {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 && Build.MODEL.contains("XIAOMI")) {
            final Object mTN = RefInvoker.getInstance().getField(toast, "mTN");
            Runnable myRun = new Runnable() {
                @Override
                public void run() {
                    try {
                        RefInvoker.getInstance().invokeMethod(
                                "handleShow", mTN, new Class[0], new Object[0]);
                    } catch (RuntimeException e) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        PrintWriter writer = new PrintWriter(outputStream);
                        e.printStackTrace(writer);
                        writer.flush();
                        if (outputStream.toString().contains("Failed to initialize display event receiver.")) {
                            try {
                                RefInvoker.getInstance().setField(mTN,
                                        "mView", null);
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (NoSuchFieldException e1) {
                                e1.printStackTrace();
                            } catch (IllegalAccessException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            throw e;
                        }
                    } catch (ClassNotFoundException e) {
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            };
            Runnable hide = new Runnable() {
                @Override
                public void run() {
                    try {
//                    MGJComLog.d(TAG, "hide");
                        RefInvoker.getInstance().invokeMethod(
                                "handleHide", mTN, new Class[0], new Object[0]);
                        RefInvoker.getInstance().setField(  mTN,
                                "mNextView", null);
//                    MGJComLog.d(TAG, "hide end");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            RefInvoker.getInstance().setField( mTN,
                    "mShow", myRun);
            RefInvoker.getInstance().setField( mTN,
                    "mHide", hide);
        }

        return toast;
    }


    @Override
    public void show() {
        if (MGPinkToastManager.getInstance().hasSameShowingToast(this)) {
            return;
        }

        if (!sNeedShow) {
            return;
        }

        // we show the toast after the last has done
        // MGPinkToastManager.getInstance().hideAllShowingToasts();
        toast.show();

        handleToastShow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowing) {
                    handleToastHide();
                }
            }
        }, toast.getDuration() == Toast.LENGTH_LONG ? 3500 : 2000);
    }

    @Override
    public void cancel() {
        toast.cancel();
        handleToastHide();
    }

    private void handleToastHide() {
        MGPinkToastManager.getInstance().removeFromCurrentShowingToasts(this);
        isShowing = false;
    }

    private void handleToastShow() {
        isShowing = true;
        MGPinkToastManager.getInstance().addCurrentShowingToast(this);
    }

    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public View getView() {
        return toast.getView();
    }

    public String getContent() {
        View childView = toast.getView();
        if (childView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) childView).getChildCount(); i++) {
                View view = ((ViewGroup) childView).getChildAt(i);
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    return tv.getText().toString();
                }
            }
        }
        return "";
    }

    public static class Builder {

        private Context mContext;

        private String mMakeText;
        private int mDuration = Integer.MIN_VALUE;
        private int mToastBgid = Integer.MIN_VALUE;
        private int mTextSize = Integer.MIN_VALUE;
        private int mTextColor = Integer.MIN_VALUE;
        private int mIconId = Integer.MIN_VALUE;
        private boolean mShowInBackground = false;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMakeText(String makeText) {
            this.mMakeText = makeText;
            return this;
        }

        public Builder setDuration(int duration) {
            this.mDuration = duration;
            return this;
        }

        public Builder setToastBgid(int mToastBgid) {
            this.mToastBgid = mToastBgid;
            return this;
        }

        public Builder setTextSize(int mTextSize) {
            this.mTextSize = mTextSize;
            return this;
        }

        public Builder setTextColor(int mTextColor) {
            this.mTextColor = mTextColor;
            return this;
        }

        public Builder setIconId(int iconId) {
            this.mIconId = iconId;
            return this;
        }

        public Builder setShowInBackground(boolean showInBackground) {
            mShowInBackground = showInBackground;
            return this;
        }

        public Builder show() {
            build(mContext, mMakeText, mDuration, mToastBgid, mTextSize, mTextColor, mIconId, mShowInBackground).show();
            return this;
        }
    }

    private static PinkToast build(Context context, CharSequence text, int duration, int tabbgId, int testsize, int testcolor, int iconId) {
        return build(context, text, duration, tabbgId, testsize, testcolor, iconId, false);
    }

    private static PinkToast build(Context context, CharSequence text, int duration, int tabbgId, int testsize, int testcolor, int iconId, boolean showInBackground) {
        ScreenTools tools = ScreenTools.instance();
        TypedValue outValue = new TypedValue();
        Drawable background = null;
        if (null != context && null != context.getTheme()) {
            context.getTheme().resolveAttribute(R.attr.mgjToastStyle, outValue, true);
            TypedArray array = context.obtainStyledAttributes(outValue.resourceId, R.styleable.MGJToastStyle);
            background = array.getDrawable(R.styleable.MGJToastStyle_android_background);
            if (testcolor == Integer.MIN_VALUE) {
                testcolor = array.getColor(R.styleable.MGJToastStyle_android_textColor, Color.WHITE);
            }
            if (testsize == Integer.MIN_VALUE) {
                testsize = array.getDimensionPixelSize(R.styleable.MGJToastStyle_android_textSize, Integer.MIN_VALUE);
                if (testsize != Integer.MIN_VALUE) {
                    testsize = tools.px2dip(testsize);
                }
            }
            if (duration != Toast.LENGTH_LONG && duration != Toast.LENGTH_SHORT) {
                duration = LENGTH_SHORT;
            }
            if (iconId == Integer.MIN_VALUE) {
                iconId = array.getResourceId(R.styleable.MGJToastStyle_android_icon, Integer.MIN_VALUE);
            }
            array.recycle();
        }

        Toast result = null;
        try {
            result = genMGToast(Toast.makeText(context, text, duration));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (tabbgId == Integer.MIN_VALUE) {
            if (background != null) {
                result.getView().setBackgroundDrawable(background);
            } else {
                result.getView().setBackgroundResource(R.drawable.toast_bg);
            }
        } else {
            result.getView().setBackgroundResource(tabbgId);
        }
        result.getView().setPadding(0, 0, 0, 0);

        result.setGravity(Gravity.CENTER, 0, 0);

        if (iconId != Integer.MIN_VALUE && text.length() <= 8) {

            //设置最小宽高
            result.getView().setMinimumWidth(tools.dip2px(180));
            result.getView().setMinimumHeight(tools.dip2px(40));

            ImageView imageView = new ImageView(context);
            imageView.setImageResource(iconId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tools.dip2px(35), tools.dip2px(35));
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.setMargins(0, tools.dip2px(20), 0, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setBackgroundColor(Color.TRANSPARENT);//fix for mx4 flyme os 4.2.2.2A

            if (result.getView() instanceof LinearLayout) {
                LinearLayout toastView = (LinearLayout) result.getView();
                toastView.setOrientation(LinearLayout.VERTICAL);
                toastView.addView(imageView, 0);
            }
        }

        View childView = result.getView();
        if (childView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) childView).getChildCount(); i++) {

                View view = ((ViewGroup) childView).getChildAt(i);
                if (view instanceof TextView && !TextUtils.isEmpty(text)) {
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    mlp.leftMargin = 0;
                    mlp.rightMargin = 0;
                    mlp.bottomMargin = 0;
                    TextView tv = (TextView) view;
                    final int padding = tools.dip2px(14);
                    //有icon的时候上下间距都为20dp
                    if (iconId != Integer.MIN_VALUE && text.length() <= 8) {
                        tv.setPadding(padding, padding, padding, padding + tools.dip2px(6));
                    } else {
                        tv.setPadding(padding, padding, padding, padding);
                    }
                    tv.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                    tv.setMinWidth(tools.dip2px(180));
                    tv.setMinHeight(tools.dip2px(40));
                    tv.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                    tv.setTextColor(testcolor);
                    if (Integer.MIN_VALUE != testsize) {
                        tv.setTextSize(testsize);
                    }
                    tv.setBackgroundColor(Color.TRANSPARENT);//fix for mx4 flyme os 4.2.2.2A
                }
            }
        }

        // if process is not foreground, don't show
        // 当文字和icon 同时没有的情况下也不弹出toast
        if (showInBackground) {
            sNeedShow = !(TextUtils.isEmpty(text) && iconId == Integer.MIN_VALUE);
        } else {
            sNeedShow = ProcessForegroundUtils.instance().isForeground(context) && !(TextUtils.isEmpty(text) && iconId == Integer.MIN_VALUE);
        }
        return initPinkToast(result, context);
    }
}
