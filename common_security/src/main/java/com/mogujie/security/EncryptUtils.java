package com.mogujie.security;

/**
 * Created by dolphinWang on 15/11/25.
 */
public class EncryptUtils {
    static {
        MGSoTool.loadLibrary("common_security");
    }

    public static native String encryptAESNative(String input);

    /**
     * The length of key must be 16.
     *
     * @param input
     * @param key
     * @return
     */
    public static native String encryptAESNativeKey(String input, String key);

    public static native String decryptAESNative(String input);

    /**
     * * The length of key must be 16.
     *
     * @param input
     * @param key
     * @return
     */
    public static native String decryptAESNativeKey(String input, String key);

    /**
     * 使用SHA1WithRSA对input进行签名，字符集编码默认UTF-8。
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static native String signWithRSANative(String input, String privateKey);

    public static native boolean verifySignatureRSANative(String input, String signature, String publicKey);

    public static native String encryptRSAWithPublicKeyNative(String input, String publicKey);

    public static native String encryptRSAWithPrivateKeyNative(String input, String privateKey);

    public static native String decryptRSAWithPublicKeyNative(String input, String publicKey);

    public static native String decryptRSAWithPrivateKeyNative(String input, String privateKey);

}
