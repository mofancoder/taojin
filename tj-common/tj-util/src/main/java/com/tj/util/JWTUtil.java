package com.tj.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Slf4j
public class JWTUtil {

    private static final Long EXPIRE_TIME = 5 * 60 * 1000L;

    private static final String secret = "90fc3d32ed4faf7e1de6f7ca782f4dcd0112e9ec9588fa7d97ae6ba39e3d1ccd";

    /**
     * 签名
     *
     * @param phone 声明 用户名
     * @return 加密的token
     */
    public static String sign(String phone) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        String jws = Jwts.builder().
                claim("phone", phone).
                signWith(Keys.hmacShaKeyFor(secret.getBytes())).setExpiration(date).compact();
        return jws;
    }

    /**
     * 校验签名是否正确
     *
     * @param token 秘钥
     * @param phone 用户名
     * @return 是否正确
     */
    public static boolean verify(String token, String phone) {
        Jwts.parser().require("phone", phone).setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).parseClaimsJws(token);
        return true;
    }

    /**
     * 获取token中的信息，无需secret解密也能获取
     *
     * @param token token
     * @return 用户名
     */
    public static String getUsername(String token) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).parseClaimsJws(token).getBody().get("phone").toString();
    }

    public static void main(String[] args) {
        try {
            String s = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret.getBytes("UTF-8")).hmacHex("this is a secret of supersac");
            System.out.println(s);
            System.out.println(s.length());
            System.out.println(s.getBytes().length);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
