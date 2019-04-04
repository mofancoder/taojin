package com.tj.util.captcha;

/**
 * GeetestWeb配置文件,mobile版本的id与key
 */
public class GeetestConfig {

    // 填入自己的captcha_id和private_key
    private static final String geetest_id = "6b07eb85d4a0d49adcd731b7a6bf11d8";
    private static final String geetest_key = "ee59d816d3c0cf9e960a9b44a4c33f86";

    public static final String getGeetest_id() {
        return geetest_id;
    }

    public static final String getGeetest_key() {
        return geetest_key;
    }

}
