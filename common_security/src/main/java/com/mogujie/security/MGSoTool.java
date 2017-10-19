package com.mogujie.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;

/**
 * Created by jianwang on 16/5/31.
 */
public class MGSoTool {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void loadLibrary(String lib) {
        int retry = 0;
        do {
            try {
                System.loadLibrary(lib);
                Log.i("MGSoTool", "System.loadLibrary success" + lib);
                break;
            } catch (Throwable t) {
                retry++;
                try {
                    Thread.sleep(50);
                } catch (Throwable tmp) {
                }
            }
        } while (retry < 2);


        if (retry >= 2) {
            boolean ifLoad = mgjLoadLibrary(lib);
            if (!ifLoad) {
                loadLibFromLocal(lib);
            }
        }

    }

    /**
     * params:
     * packgetName:应用程序包名
     * soName：希望加载的so文件名，如名为 params，则加载libparams.so
     * <p/>
     * func:
     * 在绝对路径加载so
     * <p/>
     * 注意该函数抛出IOException & linkedError
     */
    private static boolean mgjLoadLibrary(String soName) {

        try {
            int pos = mContext.getFilesDir().getAbsolutePath().lastIndexOf("/");
            String path = mContext.getFilesDir().getAbsolutePath().substring(0, pos + 1);
            Log.i("MGSoTool", "path:" + path);
            File dir = new File(path);

            if (!dir.isDirectory()) {
                Log.i("MGSoTool", "mgjLoadLibrary dir" + dir.getAbsolutePath() + " is not dir");
                return false;
            }

            boolean ifFindDir = false;
            File targetFile = null;
            for (File file : dir.listFiles()) {
                if (file.getAbsolutePath().endsWith("/lib")) {
                    ifFindDir = true;
                    targetFile = file;
                    break;
                }
            }
            if (ifFindDir) {
                String strFile = targetFile.getCanonicalPath() + "/lib" + soName + ".so";
                System.load(strFile);
                Log.i("MGSoTool", "mgjLoadLibrary load success:" + strFile);
                return true;
            }
            Log.i("MGSoTool", "mgjLoadLibrary return false");
            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    private static void loadLibFromLocal(String soName) {
        try {
            long tmp = System.currentTimeMillis();
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            String sourceDir = appInfo.sourceDir;

            ZipFile zipfile = new ZipFile(sourceDir);
            String realSo = "lib" + soName + ".so";
            int looper  = 0;
            InputStream in = null;
            for(looper = 0; looper < 3; looper++) {
                try {
                    in = zipfile.getInputStream(zipfile.getEntry("lib/armeabi-v7a/" + realSo));
                    break;
                }catch (Throwable ex){
                    if(looper == 2){
                        //如果三次加载失败 抛出异常
                        throw  ex;
                    }
                    try{
                        // sleep 20ms, 尝试第二次解压。
                        Thread.sleep(20);
                    }catch (Throwable e){

                    }
                }
            }

            File OutDir = new File(mContext.getFilesDir() + "/soTmp");
            if (!OutDir.exists()) {
                OutDir.mkdir();
            }
            File outFile = new File(OutDir, realSo);
            if (outFile.exists()) {
                outFile.delete();
            }
            OutputStream fos = new FileOutputStream(outFile);
            byte[] buf = new byte[1024 * 4];
            int length;
            while ((length = in.read(buf, 0, buf.length)) != -1) {
                fos.write(buf, 0, length);
            }
            fos.flush();
            System.load(outFile.getAbsolutePath());
            Log.i("MGSoTool", "loadLibFromLocal success and out file:" + outFile.getAbsolutePath());
            zipfile.close();
            fos.close();
            in.close();
            Log.i("MGSoTool", "loadLibFromLocal time:" + (System.currentTimeMillis() - tmp));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


}
