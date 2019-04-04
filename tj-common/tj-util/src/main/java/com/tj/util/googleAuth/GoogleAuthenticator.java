package com.tj.util.googleAuth;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

public class GoogleAuthenticator {

    /**
     * 随机生成一个密钥
     */
    public static String createSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);
        return secretKey.toUpperCase();
    }

    /**
     * 根据密钥获取验证码
     * 返回字符串是因为验证码有可能以 0 开头
     *
     * @param secretKey 密钥
     */
    public static String getTOTP(String secretKey) {
        long time = (System.currentTimeMillis() / 1000) / 30;
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey.toUpperCase());
        String hexKey = Hex.encodeHexString(bytes);
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }

    /**
     * 生成 Google Authenticator 二维码所需信息
     * Google Authenticator 约定的二维码信息格式 : otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer}
     * 参数需要 url 编码 + 号需要替换成 %20
     *
     * @param secret  密钥 使用 createSecretKey 方法生成
     * @param account 用户账户 如: example@domain.com 138XXXXXXXX
     * @param issuer  服务名称 如: Google Github 印象笔记
     */
    public static String createGoogleAuthQRCodeData(String secret, String account, String issuer) {
        String qrCodeData = "otpauth://totp/%s?secret=%s&issuer=%s";
        try {
            return String.format(qrCodeData, URLEncoder.encode(account, "UTF-8").replace("+", "%20"), URLEncoder.encode(secret, "UTF-8")
                    .replace("+", "%20"), URLEncoder.encode(issuer, "UTF-8").replace("+", "%20"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        //otpauth://totp/15179818328--05-16,15:44:52?secret=CGUTY2X5ZRK4XUNM&issuer=HUOBI
        System.out.println(createGoogleAuthQRCodeData("createSecretKey", "wew", "dffr"));
        System.out.println(createSecretKey());
        String code = getTOTP("mecwjl2u7fznzovn7fxfk4rpbpylxdzw");
        String ownCode = "236359";
        if (code.equals(ownCode)) {
            System.out.println("输入正确" + code);
        } else {
            System.out.println("输入失败" + code);
        }
    }
}
