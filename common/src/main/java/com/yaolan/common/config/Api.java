package com.yaolan.common.config;

/**
 * Created by 王冰 on 2017/8/21.
 */

public class Api {
    public static final String REGEX_MOBILE = "^1(3|4|5|7|8)\\d{9}$";

    public static final boolean DEBUG = false;

    public static final String Host = DEBUG ? "http://careworker.w-jsy.com/" : "http://www.w-lm.com/";
}
