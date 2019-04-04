package com.tj.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tj.util.validation.Valids;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoCreateUtil {
    public static int number = 0;

    /**
     * 生成订单号
     *
     * @param type        类型：DH:电话订单；ST:实体店订单；AP：app订单；JD:京东订单；MT：美团订单；EL：饿了么订单；
     * @param storeNumber 店编码
     * @return
     */
    public static String orderNumber(String type, String storeNumber) {
        String orderNumber = "";
        String param = "00000";
        if (Valids.isBlank(type))
            return orderNumber;
        orderNumber += type + "-D-" + storeNumber + "-" +
                new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-";
        number++;
        String numberString = Integer.toString(number);
        orderNumber += param.substring(0, param.length() - numberString.length() - 1) + numberString;
        return orderNumber;
    }

    public static String createToken(Integer userId) {
        return Hashing.md5().hashString(userId + "" + System.currentTimeMillis(), Charsets.UTF_8).toString();
    }
}
