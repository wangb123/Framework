package org.wbing.base.utils.FileUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by duanjin on 7/15/15.
 * 文件路径工具类，用来获取各个目录下的文件，请确保对返回值做null判断
 */
public class MGJFilePathUtils {

    /**
     * 得到app 数据文件夹
     *
     * @param ctx
     * @return 有可能返回null
     */
    public static File getAppDataDir(Context ctx) {
        if (ctx != null) {
            return ctx.getApplicationContext().getFilesDir();
        }
        return null;
    }

    /**
     * 得到app 数据文件夹下某文件
     *
     * @param ctx
     * @param fileName
     * @return 有可能返回null
     */
    public static File getAppDataFile(Context ctx, String fileName) {
        if (ctx != null) {
            File dir = getAppDataDir(ctx);
            if (dir != null) {
                return new File(dir, fileName);
            }
        }
        return null;
    }

    /**
     * 得到app的缓存目录
     *
     * @param ctx
     * @return 有可能返回null
     */
    public static File getCacheDir(Context ctx) {
        if (ctx != null) {
            return ctx.getCacheDir();
        }
        return null;
    }


    /**
     * 得到app缓存目录文件
     *
     * @param ctx
     * @param fileName
     * @return 有可能返回null
     */
    public static File getCacheFile(Context ctx, String fileName) {
        if (ctx != null) {
            File cacheDir = ctx.getCacheDir();
            if (cacheDir != null){
                return new File(cacheDir, fileName);
            }
        }
        return null;
    }

    /**
     * 获得sdcard目录
     *
     * @return 有可能返回null
     */
    public static File getExternalFileDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * 获得sdcard目录下文件,如果想添加目录，传入"/myapp/temp.txt"
     *
     * @return 有可能返回null
     */
    public static File getExternalFile(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File extDir = Environment.getExternalStorageDirectory();
            if (extDir != null) {
                File file = new File(extDir, fileName);
                return file;
            }
        }
        return null;
    }
}
