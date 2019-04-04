package com.tj.util.validation;

import org.springframework.util.StringUtils;

/**
 * Created by ldh on 2018-04-20.
 */
public class PhoneUtil {
    /**
     * 加星号混淆
     *
     * @param phone
     * @return
     */
    static public String confuse(String phone) {
        if (null != phone && phone.length() >= 10) {
            int len = phone.length();
            String repstr = phone.substring(4, len - 4);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < repstr.length(); ++i) {
                sb.append("*");
            }
            phone = StringUtils.replace(phone, repstr, sb.toString());
        }

        return phone;
    }
}
