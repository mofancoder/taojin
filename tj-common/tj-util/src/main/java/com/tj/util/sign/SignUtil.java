package com.tj.util.sign;

import com.google.common.base.Charsets;
import com.google.common.collect.Ordering;
import com.google.common.hash.Hashing;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 签名类
 */
public class SignUtil {

    /**
     * 验证签名
     *
     * @param request 请求
     * @param signKey 私钥
     * @return
     */
    public static boolean verify(HttpServletRequest request, String signKey) {
        //获取所有请求参数
        if (null == request || null == signKey) {
            return false;
        }
        Enumeration<String> paramKeys = request.getParameterNames();
        Map<String, String> params = new HashMap<>();
        while (paramKeys.hasMoreElements()) {
            String key = paramKeys.nextElement();
            params.put(key, request.getParameter(key));
        }
        if (!params.containsKey("_sign")) {
            return false;
        } else {
            //获取当前key
            String key = params.remove("_sign");
            //对参数进行加密
            String currentKey = sign(params, signKey);
            //对比
            return Objects.equals(key, currentKey);
        }
    }

    //生成签名
    public static String sign(Map<String, String> params, String key) {
        List<String> keys = Ordering.usingToString().sortedCopy(params.keySet());//key排序
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            sb.append(k).append("=").append(params.get(k)).append("&");
        }
        sb.append("key").append("=").append(key);
        return Hashing.md5().hashString(sb, Charsets.UTF_8).toString();
    }

    public static String md5pwdSalt(String md5pwd, String salt) {
        StringBuilder sb = new StringBuilder();
        sb.append(md5pwd).append(salt);
        return Hashing.md5().hashString(sb, Charsets.UTF_8).toString();
    }

    /**
     * 根据文件计算出文件的MD5
     *
     * @param in
     * @return
     */
    public static String getFileMD5(InputStream in) {

        MessageDigest digest = null;

        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");

            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());

        return bigInt.toString(16);
    }

    public static String SHA256(final String strText) {
        String strType = "SHA-256";
        // 返回值
        String strResult = null;
        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {
            try {   // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 類型结果
                byte byteBuffer[] = messageDigest.digest();
                // 將 byte 轉換爲 string
                StringBuffer strHexString = new StringBuffer();
                // 遍歷 byte buffer
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return strResult;
    }

}
