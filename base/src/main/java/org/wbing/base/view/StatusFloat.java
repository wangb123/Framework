package org.wbing.base.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.wbing.base.utils.ScreenTools;

import java.util.Iterator;
import java.util.LinkedList;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

/**
 * Created by 6a209 on 15/1/22.
 * <p/>
 * 一个覆盖status bar 的view 用来调试
 */
public class StatusFloat {

    private static StatusFloat sStatusFloat;

    private Context mContext;
    private TextView mTv;
    private LinkedList<String> contentList;

    private StatusFloat(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    public static StatusFloat instance(Context context) {
        if (null == sStatusFloat) {
            sStatusFloat = new StatusFloat(context);
        }
        return sStatusFloat;
    }

    private void init() {
        contentList=new LinkedList<>();
        mTv = new TextView(mContext);
        mTv.setClickable(false);
        mTv.setTextColor(Color.WHITE);
        mTv.setTextSize(10);
        mTv.setBackgroundColor(Color.parseColor("#804e504b"));
        final int statusBarHeight = ScreenTools.instance(mContext).getStatusBarHeight() * 10;

        WindowManager mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_NO_LIMITS | FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN | FLAG_NOT_TOUCHABLE,
                TRANSLUCENT
        );
        mParams.gravity = Gravity.TOP;
        mWm.addView(mTv, mParams);
        hide();
    }

    public void show() {
        mTv.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mTv.setVisibility(View.GONE);
    }

    public boolean isShow() {
        return mTv.isShown();
    }

    public void updateText(String str) {
        if(null == contentList ){
            contentList=new LinkedList<>();
        }
        while(contentList.size() > 3){
            contentList.removeFirst();
        }
        contentList.add(str);

        final StringBuilder mContent = new StringBuilder(128);
        Iterator<String> iterator = contentList.iterator();
        while (iterator.hasNext()) {
            mContent.append(iterator.next()).append("\n------------\n");;
        }
        if (mTv.isShown()) {
            mTv.post(new Runnable() {
                @Override
                public void run() {
                   mTv.setText(mContent.toString());
                }
            });

        }
        // 3 lines
//        if (mLineCount == 3) {
//            mLineCount = 0;
//            mContent.setLength(0);
//        }
    }
    public static void addText(String str){
        if(null!=sStatusFloat){
            sStatusFloat.updateText(str);
        }
}
    public void destroy() {
        WindowManager mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWm.removeView(mTv);
        sStatusFloat = null;
    }
}
