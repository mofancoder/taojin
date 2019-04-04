package com.tj.util;


import com.tj.util.validation.Valids;

import java.lang.reflect.Field;

/**
 * create by lh 2018-4-12
 */
public class ObjectUtil {

    /**
     * 将object中的“”换成null
     *
     * @param t
     * @param <T>
     */
    public static <T> void format(T t) {
        Class stuCla = (Class) t.getClass();// 得到类对象
        Field[] fs = stuCla.getDeclaredFields();//得到属性集合
        for (Field f : fs) {//遍历属性
            f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
            Object val = null;// 得到此属性的值
            try {
                val = f.get(t);
                if (null != val && "".equals(val.toString().trim())) {
                    f.set(t, null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static String hideOwnPhone(String phone) {

        if (Valids.isPhone(phone)) {
            String temp = phone.substring(3, 7);
            phone = phone.replaceAll(temp, "****");
        }
        return phone;
    }

    public static String hideOtherPhone(String phone) {

        if (Valids.isPhone(phone)) {
            String temp = phone.substring(0, 7);
            phone = phone.replaceAll(temp, "*******");
        }
        return phone;
    }
}
