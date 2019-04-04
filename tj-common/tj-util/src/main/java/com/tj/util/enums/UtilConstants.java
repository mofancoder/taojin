package com.tj.util.enums;

import com.google.common.collect.Maps;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by ldh on 2018-01-30.
 */
public interface UtilConstants {
    public static <T> T getEnumFromName(Class<T> clazz, String name) {
        //Enum.valueOf(clazz,name);
        Object obj = null;
        try {
            //Method[] valueOfs = clazz.getMethods();
            Method valueOf = clazz.getMethod("valueOf", String.class);
            obj = valueOf.invoke(clazz, name);
        } catch (Exception ex) {
            return null;
        }
        return (T) obj;
    }

    public static class UserOptConditions {
        //登陆密码认证-1|资金密码认证-2|身份证认证-4|邮箱认证-8|谷歌认证-16|短信认证-32
        public static final int loginPwd = 1;
        public static final int fundPwd = 2;
        public static final int idCardNo = 4;
        public static final int email = 8;
        public static final int google = 16;
        public static final int message = 32;

        public static boolean needLoginPwd(int necessary) {
            return loginPwd == (necessary & loginPwd);
        }

        public static boolean needFundPwd(int necessary) {
            return fundPwd == (necessary & fundPwd);
        }

        public static boolean needIdCardNo(int necessary) {
            return idCardNo == (necessary & idCardNo);
        }

        public static boolean needEmail(int necessary) {
            return email == (necessary & email);
        }

        public static boolean needGoogle(int necessary) {
            return google == (necessary & google);
        }

        public static boolean needMessage(int necessary) {
            return message == (necessary & message);
        }

        /*
                public static boolean needLoginPwd(int fact,int expect){
                    return loginPwd != (fact & loginPwd & expect);
                }

                public static boolean needFundPwd(int fact,int expect){
                    return fundPwd != (fact & fundPwd & expect);
                }
                public static boolean needIdCardNo(int fact,int expect){
                    return idCardNo != (fact & idCardNo & expect);
                }

                public static boolean needEmail(int fact,int expect){
                    return email != (fact & email & expect);
                }
                public static boolean needGoogle(int fact,int expect){
                    return google != (fact & google & expect);
                }
                public static boolean needMessage(int fact,int expect){
                    return message != (fact & message & expect);
                }
        */
        public static boolean needLoginPwd(int fact, int expect) {
            return needLoginPwd(expect - fact);
        }

        public static boolean needFundPwd(int fact, int expect) {
            return needFundPwd(expect - fact);
        }

        public static boolean needIdCardNo(int fact, int expect) {
            return needIdCardNo(expect - fact);
        }

        public static boolean needEmail(int fact, int expect) {
            return needEmail(expect - fact);
        }

        public static boolean needGoogle(int fact, int expect) {
            return needGoogle(expect - fact);
        }

        public static boolean needMessage(int fact, int expect) {
            return needMessage(expect - fact);
        }

        public static Map<String, Integer> needWhichVerify(int fact, int expect) {
            Map<String, Integer> result = Maps.newHashMap();
            if (UtilConstants.UserOptConditions.needLoginPwd(fact, expect)) {
                result.put("needLoginPwd", 1);
            }
            if (UtilConstants.UserOptConditions.needFundPwd(fact, expect)) {
                result.put("needFundPwd", 1);
            }
            if (UtilConstants.UserOptConditions.needIdCardNo(fact, expect)) {
                result.put("needIdCardNo", 1);
            }
            if (UtilConstants.UserOptConditions.needEmail(fact, expect)) {
                result.put("needEmail", 1);
            }
            if (UtilConstants.UserOptConditions.needGoogle(fact, expect)) {
                result.put("needGoogle", 1);
            }
            if (UtilConstants.UserOptConditions.needMessage(fact, expect)) {
                result.put("needMessage", 1);
            }
            return result;
        }
    }

    public static class InsertDBCode {
        public static final int duplicate_key = -1;


        public static final int other = -1000;
    }

}
