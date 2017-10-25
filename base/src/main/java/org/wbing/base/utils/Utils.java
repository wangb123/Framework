package org.wbing.base.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 6a209 on 9/6/13.
 */
public class Utils {
    private static long lastClickTime;
    /**
     * 点击按钮响应间隔时间－毫秒
     */
    private static int INTERVAL_TIME = 500;

    /**
     * @return 判断两次响应按钮间隔
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < INTERVAL_TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 获取手机状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 显示|隐藏输入法
     *
     * @param context
     * @param view
     * @param show
     */
    public static void togleSoftInput(Context context, View view, boolean show) {
        if (context == null || view == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            view.setFocusable(true);
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }


    /**
     * 获取 数量 k
     *
     * @param count
     * @return
     */
    public static String getCountStr(int count) {
        if (count > 10000 && count < 100000) {
            int y2 = count % 100;
            if (y2 > 0) {
                count += 100;
            }
            int y4 = count / 1000;
            int y3 = count % 1000 / 100;
            if (y3 == 0) {
                return y4 + "k";
            } else {
                return y4 + "." + y3 + "k";
            }
        } else if (count >= 100000) {
            float fc = count / 1000f;
            return new DecimalFormat("#0").format(fc) + "k";
        }
        return String.valueOf(count);
    }

    /**
     * 发送广播 通知 系统更新相册
     */
    public static void broadcastSysCamera(Context context, String filename) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Video.Media.DATA, filename);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filename)));
    }

    /**
     * 英文算0.5;中文算1;
     *
     * @return
     */
    public static int getCnEnCount(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            float len = 0;
            for (int i = 0; i < text.length(); i++) {
                int tmp = (int) text.charAt(i);
                if (tmp > 0 && tmp < 127) {
                    len += 0.5f;
                } else {
                    len++;
                }
            }
            return Math.round(len);
        }
        return 0;
    }

    /**
     * 英文算1个，中文算两个
     *
     * @param text   输入的文本
     * @param length 文本的长度
     * @return 被截取后的文本
     */
    public static String getCnEnByCount(CharSequence text, int length) {
        String s = "";

        if (length <= 0) {
            return "";
        }
        s = text.toString();
        if (!TextUtils.isEmpty(text)) {
            int len = 0;
            for (int i = 0; i < text.length(); i++) {
                int tmp = (int) text.charAt(i);
                if (tmp > 0 && tmp < 127) {
                    len += 1;
                    if (len > length) {
                        return s.substring(0, i);
                    }
                } else {
                    len += 2;
                    if (len > length) {
                        return s.substring(0, i);
                    }
                }
            }
        }
        return s;
    }

    /**
     * 时间转换
     *
     * @param time
     * @return
     */
    public static String formatDate(long time) {
        SimpleDateFormat sdf;
        Date comm_date = new Date(time);
        Date current = new Date();
        if (comm_date.getYear() == current.getYear() && comm_date.getMonth() == current.getMonth() && comm_date.getDate() == current.getDate()) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("MM月dd日");
        }
        return sdf.format(new Date(time));
    }

    /**
     * 时间转换
     *
     * @param time
     * @return
     */
    public static String formatDateForPunch(String time) {
        Long longTime = Long.parseLong(time);
        Date date = new Date(longTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    public static String formatDateForPunchDetail(String time) {
        Long longTime = Long.parseLong(time);
        Date date = new Date(longTime);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(date);
    }


}
