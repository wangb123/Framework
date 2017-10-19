package org.wbing.base.utils.FileUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import org.wbing.base.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duanjin on 7/14/15.
 * 提供基本的文件读取操作，包括读取整个文件为String， 读取整个文件为String List，判断文件是否存在，
 * 创建文件，拷贝文件等
 */
public class MGJFileUtils {

    private static String TAG = "MGFileUtils";

    /**
     * 读取asset文件夹下文件内容，并以string对象返回
     * 若文件不存在，则会抛出FileNotFoundException
     * @param ctx
     * @param fileName
     * @return
     */
    public static String readAssetFileToString(Context ctx, String fileName) {
        if(ctx != null && !TextUtils.isEmpty(fileName)) {
            AssetManager assetManager = ctx.getAssets();
            if (assetManager != null) {
                try {
                    InputStream inputStream = assetManager.open(fileName);
                    return readFileToString(inputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            }

        }
        return "";
    }

    /**
     * 读取asset文件夹下内容，并以List形式返回
     * @param ctx
     * @param fileName
     * @return
     */
    public static List<String> readAssetFileToStrings(Context ctx, String fileName) {
        if (ctx != null && !TextUtils.isEmpty(fileName)) {
            AssetManager assetManager = ctx.getAssets();
            if (assetManager != null) {
                try {
                    InputStream inputStream = assetManager.open(fileName);
                    return readLines(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * 读取文件内容作为一个String对象返回，注意不要使用此方法读取很大的文件
     *
     * 另外，这个方法返回的String是去掉了每个line terminator的，一般来说可能
     * 往往不是你想要的，那么请参考另一个实现{@link #fileToString(File)}。
     * @param fileName
     * @return
     */
    public static String readFileToString(String fileName){
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            if (!file.exists() || file.isDirectory()) {
                LogUtils.d("file not exist or is a directory");
                return "";
            }
            return readFileToString(file);
        }

        LogUtils.d(  "can not pass in empty file name");
        return "";
    }

    /**
     * 读取文件内容作为一个String对象返回，注意不要使用此方法读取很大的文件
     *
     * 另外，这个方法返回的String是去掉了每个line terminator的，一般来说可能
     * 往往不是你想要的，那么请参考另一个实现{@link #fileToString(File)}。
     * @param file
     * @return
     */
    public static String readFileToString(File file){
        if (file != null) {
            if (!file.exists() || file.isDirectory()) {
                LogUtils.d( "file not exist or is a directory");
                return "";
            }
            InputStream inputStream = null;
            String fileString = "";
            try{
                inputStream = openInputStream(file);
                if(inputStream != null){
                    fileString = readFileToString(inputStream);
                }
            }catch (Exception e){

            }finally {
                try{
                    if(inputStream != null)
                        inputStream.close();
                } catch(IOException e){

                }

            }
            return fileString;

        }
        LogUtils.d(  "can not pass in null object");
        return "";
    }

    /**
     * 将输入流读取为一个String对象返回，注意，不要使用此方法读取大文件
     * @param inputStream
     * @return
     */
    private static String readFileToString(InputStream inputStream) {
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            return readFileToString(reader);
        }

        LogUtils.d(  "can not pass in null object");
        return "";
    }

    /**
     * 将reader指向的输入流读取为一个String对象返回，注意，不要使用此方法读取大文件
     * @param reader
     * @return
     */
    private static String readFileToString(Reader reader) {
        if (reader != null) {
            BufferedReader bufferedReader = toBufferedReader(reader);
            if (bufferedReader == null) {
                return null;
            }
            String line = "";
            StringBuilder sb = new StringBuilder();

            try {
                line = bufferedReader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = bufferedReader.readLine();
                }
                String result = sb.toString();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        LogUtils.d( "can not pass in null object");
        return "";
    }

    /**
     * 按行读取文件的内容，并将其组织为list返回
     * @param fileName
     *        文件的绝对路径，非空，
     * @return
     *        若传入文件名为目录或者文件不存在，则返回空列表
     */
    public static List<String> readFileToStrings(String fileName) {

        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            if (!file.exists() || file.isDirectory()) {
                LogUtils.d( "file " + fileName + "does not exist or is a directory");
                return new ArrayList<String>();
            } else {
                return readFileToStrings(file);
            }
        }
        LogUtils.d(  "can not pass in empty file name");
        return new ArrayList<>();
    }

    /**
     * 按行读取文件的内容，并将其组织为list返回
     * @param file
     *        File文件对象
     * @return
     *        若传入文件名为目录或者文件不存在，则返回空列表
     */
    public static List<String> readFileToStrings(File file) {
        if (file == null) {
            return new ArrayList<String>();
        }
        InputStream inputStream = null;
        List<String> fileString = new ArrayList<>();
        try{
            inputStream = openInputStream(file);
            if(inputStream != null){
                fileString = readLines(inputStream);
            }
        }catch (Exception e){

        }finally {
            try{
                if(inputStream != null)
                    inputStream.close();
            } catch(IOException e){

            }

        }
        return fileString;
    }


    /**
     * 针对传入的file，获得FileInputStream对象
     * @param file
     * @return
     *        函数有可能返回null
     */
    private static FileInputStream openInputStream(File file) {
        if (file == null || file.isDirectory() || !file.exists()) {
            return null;
        }
        try {
            FileInputStream fip = new FileInputStream(file);
            return fip;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 针对传入的File，创建对应的BufferedWriter对象
     * @param file
     * @return
     *        函数有可能返回null
     */
    private static BufferedWriter openOutputWriter(File file, boolean append){
        if (file == null || file.isDirectory() || !file.exists()) {
            return null;
        }

        try {
            if (file.canWrite()) {
                FileWriter fw = new FileWriter(file, append);
                BufferedWriter bw = new BufferedWriter(fw);
                return bw;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将inputstream中的数据以list的形式返回
     * @param inputStream
     * @return
     */
    private static List<String> readLines(InputStream inputStream) {
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            return readLines(reader);
        }
        return new ArrayList<String>();
    }

    /**
     * 从reader中按行读取数据，以列表形式返回
     * @param reader
     * @return
     */
    private static List<String> readLines(Reader reader) {
        BufferedReader bufferedReader = toBufferedReader(reader);
        List<String> strings = new ArrayList<>();
        if (bufferedReader != null) {
            try {
                String line = bufferedReader.readLine();
                while (line != null) {
                    strings.add(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                strings.clear();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        return strings;
    }

    /**
     * 将Reader包装为BufferedReader
     * @param reader
     * @return
     */
    private static BufferedReader toBufferedReader(Reader reader) {
        if (reader == null) {
            return null;
        }
        BufferedReader bufferedReader = (reader instanceof BufferedReader) ? (BufferedReader)reader
                : new BufferedReader(reader);
        return bufferedReader;
    }

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    public static String fileToString(File file) {
        return fileToString(file, Charset.defaultCharset());
    }

    /**
     * refer to Apache commons-io v2.5, FileUtils#readFileToString & IOUtils.java
     * read file content as a whole string, unlike {@link #readFileToString(File)}, this method's return including line terminator
     */
    public static String fileToString(File file, Charset charset) {
        FileInputStream fis = openInputStream(file);
        if (fis == null) {
            return "";
        }

        InputStreamReader charReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            charReader = new InputStreamReader(fis, charset);
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n;
            while (EOF != (n = charReader.read(buffer))) {
                sb.append(buffer, 0, n);
            }
        } catch (IOException e) {
            // if exception, we rollback to initial state
            sb.delete(0, sb.length());
        } finally {
            closeQuietly(charReader);
        }
        return sb.toString();
    }

    /**
     * 得到可以逐行遍历文件的iterator
     * 注意，需要对返回值做“空检查”，在使用iterator后，需要手动调用close方法
     * @param filename
     *        文件绝对路径
     * @return
     *        若传入参数不合法，则返回空对象
     */
    public static LineIterator getLineIterator(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }

        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            LogUtils.d(  "file not exist or is directory");
            return null;
        }

        return getLineIterator(file);
    }

    /**
     * 得到可以逐行遍历文件的iterator
     * 注意，需要对返回值进行“空判断”，在使用iterator后需要手动调用close方法
     * @param file
     * @return
     *        若传入参数不合法，则返回空对象,在使用iterator后，需要手动调用close方法
     */
    public static LineIterator getLineIterator(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }

        InputStream inputStream = openInputStream(file);
        if (inputStream == null) {
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return  new LineIterator(inputStreamReader);
    }

    /**
     * 将content写入文件fileName当中，不会采用append模式，若文件不存在，则创建文件
     * @param content
     * @param fileName
     * @return
     *        是否正常写入文件
     */
    public static boolean writeStringToFile(String content, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            LogUtils.d( "can not pass in empty string");
            return false;
        }
       return writeStringToFile(content, fileName, false);
    }

    /**
     * 将content写入文件内容当中，确保使用文件绝对路径,若未见不存在，会创建文件
     * @param content
     *        需要写入文件的内容
     * @param fileName
     *        文件名
     * @param append
     *        true表示在文件后面添加，false表示覆盖文件内容
     */
    public static boolean writeStringToFile(String content, String fileName, boolean append) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(fileName)) {
            LogUtils.d( "can not pass in empty string");
            return false;
        }
        File file = new File(fileName);
        return writeStringToFile(content, file, append);
    }

    public static boolean writeStringToFile(String content, File file) {
        return writeStringToFile(content, file, false);
    }

    /**
     * like {@link #writeStringToFile(String, File)}}, except it supports write empty
     * string to file, in case you may want to erase/clear file content.
     */
    public static boolean writeAllStringToFile(String content, File file) {
        if (file == null) {
            return false;
        }
        // make sure even content is empty, we can still allow write it to file,
        // to support some kind of erase/clear file content.
        if (content == null) {
            content = "";
        }
        return writeStringToFileInternal(content, file, false);
    }

    public static boolean writeStringToFile(String content, File file, boolean append) {
        if (TextUtils.isEmpty(content) || file == null) {
            return false;
        }
        return writeStringToFileInternal(content, file, append);
    }

    private static boolean writeStringToFileInternal(String content, File file, boolean append) {
        //如果文件不存在，则创建文件
        File parent = file.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return false;
            }
        }
        if (!file.exists()) {
            try {
                boolean res = file.createNewFile();
                if (!res) {
                    return res;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        boolean result = false;
        //如果文件可写
        if (file.canWrite()) {
            BufferedWriter bw = openOutputWriter(file, append);
            if (bw != null) {
                try {
                    bw.write(content);
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断文件是否已经存在，确保使用文件绝对路径
     * @param fileName
     * @return
     */
    public static boolean isFileExists(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            LogUtils.d(  "can not pass in empty string");
            return false;
        }
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 创建文件，若所在文件夹不存在，则创建路径上所需文件夹
     * @param fileName
     * @return
     *        文件创建成功则返回true，若文件已经存在或者创建失败则返回false
     */
    public static boolean createFile(String fileName) {
        if (!TextUtils.isEmpty(fileName)){
            File file = new File(fileName);
            return createFile(file);
        }
        return false;
    }

    /**
     * 创建新文件，若所在文件夹不存在，则创建路径上所需文件夹
     * @param file
     * @return
     *        文件创建成功返回true，若文件已经存在或者创建不成功则返回false
     */
    public static boolean createFile(File file){
        if (file != null) {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    if (parent.mkdirs()){
                        try {
                            return file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * 复制文件，给出绝对文件路径,如果dest文件已经存在，则会被覆盖
     * @param src
     *        /data/data/temp.txt
     * @param dest
     *        /cache/temp/temp.txt
     */
    public static boolean copyFile(String src, String dest) {
        if (!TextUtils.isEmpty(src) && !TextUtils.isEmpty(dest)) {
            File srcFile = new File(src);
            File destFile = new File(dest);
            return copyFile(srcFile, destFile);
        }
        return false;
    }

    /**
     * 复制文件，给出有效文件对象，如果dest文件已经存在，则会被覆盖
     * @param src
     * @param dest
     */
    public static boolean copyFile(File src, File dest) {
        if (src == null) {
            LogUtils.d( "src is null");
            return false;
        }
        if (src.isDirectory() || !src.exists()) {
            LogUtils.d(  "can not copy directory");
            return false;
        }

        if (dest == null) {
            LogUtils.d(  "des can not be null");
            return false;
        }

        File parent = dest.getParentFile();
        if (parent != null) {
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    LogUtils.d( "can not create directory");
                    return false;
                }
            }
        }
        if (dest.exists() && !dest.canWrite()){
            LogUtils.d(  "dest file can not be written");
            return false;
        }
        return doCopyFile(src, dest);
    }

    /**
     * @param src
     * @param dest
     * @return
     */
    private static boolean doCopyFile(File src, File dest) {
        if (dest.exists() && dest.isDirectory()) {
            LogUtils.d(  "dest is a directory");
            return false;
        }
        FileInputStream fin = null;
        FileOutputStream fout = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fin = new FileInputStream(src);
            fout = new FileOutputStream(dest);
            inChannel = fin.getChannel();
            outChannel = fout.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inChannel != null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (src.length() != dest.length()) {
            return false;
        }
        return true;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
                // intentional ignore
            }
        }
    }
}
