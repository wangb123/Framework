package org.wbing.base.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log统一管理类
 */
public class LogUtils {

    public static String customTagPrefix = "";
    public static boolean debug = true;
    public static CustomLogger customLogger;

    private LogUtils() {
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, new Object[]{callerClazzName, caller.getMethodName(), Integer.valueOf(caller.getLineNumber())});
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.d(tag, content);
            } else {
                Log.d(tag, content);
            }

        }
    }

    public static void d(String content, Throwable tr) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.d(tag, content, tr);
            } else {
                Log.d(tag, content, tr);
            }

        }
    }

    public static void e(String content) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.e(tag, content);
            } else {
                Log.e(tag, content);
            }

        }
    }

    public static void e(String content, Throwable tr) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.e(tag, content, tr);
            } else {
                Log.e(tag, content, tr);
            }

        }
    }

    public static void i(String content) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.i(tag, content);
            } else {
                Log.i(tag, content);
            }

        }
    }

    public static void i(String content, Throwable tr) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.i(tag, content, tr);
            } else {
                Log.i(tag, content, tr);
            }

        }
    }

    public static void v(String content) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.v(tag, content);
            } else {
                Log.v(tag, content);
            }

        }
    }

    public static void v(String content, Throwable tr) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.v(tag, content, tr);
            } else {
                Log.v(tag, content, tr);
            }

        }
    }

    public static void w(String content) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.w(tag, content);
            } else {
                Log.w(tag, content);
            }

        }
    }

    public static void w(String content, Throwable tr) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.w(tag, content, tr);
            } else {
                Log.w(tag, content, tr);
            }

        }
    }

    public static void w(Throwable tr) {
        if (debug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            if (customLogger != null) {
                customLogger.w(tag, tr);
            } else {
                Log.w(tag, tr);
            }

        }
    }

    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }


    public interface CustomLogger {
        void d(String var1, String var2);

        void d(String var1, String var2, Throwable var3);

        void e(String var1, String var2);

        void e(String var1, String var2, Throwable var3);

        void i(String var1, String var2);

        void i(String var1, String var2, Throwable var3);

        void v(String var1, String var2);

        void v(String var1, String var2, Throwable var3);

        void w(String var1, String var2);

        void w(String var1, String var2, Throwable var3);

        void w(String var1, Throwable var2);
    }
}