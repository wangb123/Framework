package org.wbing.base.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.mogujie.security.EncryptUtils;

import org.wbing.base.utils.FileUtils.MGJFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 加密工具类
 *
 * @author 6a209 11:04:05 AM Jul 10, 2012
 */
public class EncryptUtil {

    private static EncryptUtil sEncrypt;

    private EncryptUtil() {

    }

    public static EncryptUtil instance() {
        if (null == sEncrypt) {
            sEncrypt = new EncryptUtil();
        }
        return sEncrypt;
    }

    /**
     * md5加密
     * String to md5
     *
     * @param str
     * @return md5(str) or empty string if exception
     */
    public String strToMD5(String str) {
        if (str == null) {
            return "";
        }
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return toHexStr(messageDigest.digest());
    }

    private String toHexStr(byte[] bytes) {
        StringBuilder md5sb = new StringBuilder();
        for (byte b : bytes) {
            // details refer to:
            // 1. http://www.avajava.com/tutorials/lessons/how-do-i-generate-an-md5-digest-for-a-string.html
            // 2. http://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
            md5sb.append(String.format("%02x", b & 0xff));
        }
        return md5sb.toString();
    }

    /**
     * @deprecated use {@link #strToMD5(String)} instead.
     */
    @Deprecated
    public String strToMD5_32(String str) {
        return strToMD5(str);
    }

    public String strToMD5_16(String str) {
        String md5_32 = strToMD5_32(str);
        if (!TextUtils.isEmpty(md5_32) && md5_32.length() >= 32) {
            return md5_32.substring(8, 24);
        }
        return "";
    }

    /**
     * file to md5
     *
     * @param file
     * @return
     */
    public String fileToMD5(File file) {
        if (file == null || !file.isFile()) {
            return "";
        }
        MessageDigest messageDigest = null;
        InputStream fis = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, numRead);
            }
            return toHexStr(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MGJFileUtils.closeQuietly(fis);
        }
        return "";
    }

    //!!  -------------- md5 -------


    // ----------------- AES ----------------


    /**
     * Use {@link EncryptUtils#encryptAESNative(String)} instead!
     *
     * @param input
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    @Deprecated
    public String encryptAes(String input, String key) throws UnsupportedEncodingException {
        return EncryptUtils.encryptAESNativeKey(input, key);
    }

    /**
     * Use {@link EncryptUtils#decryptAESNative(String)} instead!
     *
     * @param input
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    @Deprecated
    public String decryptAes(String input, String key) throws UnsupportedEncodingException {
        return EncryptUtils.decryptAESNativeKey(input, key);
    }

    // !!! ---------------


    // ------------ RSA -------


    /**
     * RSA 加密
     *
     * @param input
     * @return
     * @throws Exception
     */
    public String encryptRSA(String input) throws Exception {
        PublicKey publicKey = getPublicKey(PRIVATE_KEY);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //加解密类
        byte[] plainText = input.getBytes("UTF-8"); //明文
        cipher.init(Cipher.ENCRYPT_MODE, publicKey); //加密
        byte[] enBytes = cipher.doFinal(plainText);
        byte[] e = Base64.encode(enBytes, Base64.DEFAULT);
        return new String(e);//Base64.encode(e, Base64.DEFAULT));
    }

    // 考虑放到so 包里面去~~~~
    private static final String PRIVATE_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClvdUhP6EMMXJw3iApoGzqh7AS\r" +
                    "GTrkHLHGJYiK8dR5QDqK4NB2jH3+G3BEuMiasN3nKR5sEDZmH8MyqJEq0AyQl8wJ\r" +
                    "rR9nhACurJzPiCZgkjLiKK+X309m+tusI8f6VBieOwFIOKdExO/g6d5ClatLkIDk\r" +
                    "Ued5G3KyupwMVAWJBQIDAQAB";

    private PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    private PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }


    private String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = Base64.encodeToString(keyBytes, 0);
        return s;
    }

}
