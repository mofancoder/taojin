package com.tj.util.captcha;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class YunPianSendMsg {
    private static String URI_SEND_SMS =
            "https://sms.yunpian.com/v2/sms/single_send.json";
    private static String ENCODING = "UTF-8";
    private static String apikey = "781ce66018d237bd47c6d6422f749ede";

    /**
     * 返回0表示成功，其他失败
     *
     * @param text
     * @param mobile
     * @return
     * @throws IOException
     */
    public static int sendSms(String text,
                              String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    private static int post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<
                        NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(),
                            param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList,
                        ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, ENCODING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println(responseText);
        log.debug(responseText);
        JSONObject json = JSONObject.parseObject(responseText);
        return json.getIntValue("code");
    }

    /**
     * 发送验证码
     *
     * @param code
     * @param phone
     * @param time
     */
    public static int sendCaptcha(String code, String phone, int time) {
        try {
            String msg = "【Sacbox】您的验证码是" + code + "，" + time + "分钟内有效。若非本人操作，请忽略本短信";

            return sendSms(msg, phone);
        } catch (Exception e) {
            log.error("", e);
        }
        return -1;
    }

    /**
     * 发送验证码
     *
     * @param code
     * @param phone
     * @param time
     */
    public static int sendSuperSacCaptcha(String code, String phone, int time, String message) {
        try {
            String msg = MessageFormat.format(message, code, time);
            return sendSms(msg, phone);
        } catch (Exception e) {
            log.error("", e);
        }
        return -1;
    }

    public static int sendTRADECaptcha(String code, String phone, int time) {
        try {
            String msg = "【AnnBit】您的验证码是" + code + "，" + time + "分钟内有效。若非本人操作，请忽略本短信。";
            return sendSms(msg, phone);
        } catch (Exception e) {
            log.error("", e);
        }
        return -1;
    }

    public static void sendForceLoginNotice(String loginAddress, String phone) {
        String phoneSuffix = phone.substring(7);
        String noticeText = "【Sacbox】安全提示：您的账号***" + phoneSuffix + "在" + loginAddress + "登录。若非本人操作，请及时改密码";
        if (!IsChinesePhone(phone)) {
            noticeText = "【Sacbox】Safety  Tips:Your account***" + phoneSuffix + " has login to" + loginAddress + ".if it is not your own operation，please modify the password in time.";
        }
        try {
            sendSms(noticeText, phone);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 资金密码类型
     *
     * @param phone
     * @param pwdType 资金或者登录
     */
    public static void sendErrorPwdNotice(String phone, String pwdType) {
        String phoneSuffix = phone.substring(7);
        String noticeText = "【Sacbox】安全提示：您的账号***" + phoneSuffix + "尝试输入" + pwdType + "密码错误次数已超上限，账户已冻结。若非本人操作，请加强密码安全";
        if (!IsChinesePhone(phone)) {
            noticeText = "【Sacbox】Safety  Tips:The number of incorrect attempts to enter the password for your account has exceeded the upper limit,and the account has been frozen . If it is not your own operation,please strengthen the safety of your password.";
        }
        try {
            sendSms(noticeText, phone);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static void sendPayNotice(String phone, String coinName, BigDecimal coinAmount) {
        String phoneSuffix = phone.substring(7);
        String amount = coinAmount.stripTrailingZeros().toPlainString();
        String noticeText = "【Sacbox】安全提示：您的账户***" + phoneSuffix + " 已付款" + amount + "个币。若非本人操作，请及时修改资金密码";
        if (!IsChinesePhone(phone)) {
            noticeText = "【Sacbox】Safety  Tips:Your account***" + phoneSuffix + " has paid " + amount + " " + coinName + ".If it is not your operation,please modify the fund password in time.";
        }
        try {
            sendSms(noticeText, phone);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static int sendEnglishCaptcha(String code, String phone, int time) {
        try {
            String msg = "【Sacbox】Your validation code is " + code + ",effective within " + time + " minutes,If you do not operate, please ignore this text message.";
            return sendSms(msg, phone);
        } catch (Exception e) {
            log.error("", e);
        }
        return -1;
    }

    public static void sendAlarm(String phone, Integer num, String name) {
        String noticeText = "【SAC管理后台】您有" + num + "个转账申请" + name + "确认，请及时查看！";
        try {
            sendSms(noticeText, phone);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("", e);
        }
    }

    public static boolean IsChinesePhone(String phone) {
        if (phone.contains("+") && !phone.contains("+86")
                && !phone.contains("+886")
                && !phone.contains("+852")
                && !phone.contains("+853")
        ) {
            return false;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        try {
			 /*for(int i=0;i<10;i++) {
				 try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				 
			 }*/
            //sendSms("【sac盒子】您的验证码是hh"+0+"，5分钟内有效。如非本人操作，请忽略本短信", "13557832237");
            //sendForceLoginNotice("广东省广州市", "13557832237");
            // sendPayNotice("13557832237", "SAC", BigDecimal.valueOf(600));
            //sendEnglishCaptcha("125612", "0016267807552", 5);
            //sendForceLoginNotice("广东广州省","13510946325");
            //sendAlarm("13557832237", 1, "上链 blockResult is null");
            //int i = sendSuperSacCaptcha("1111", "+8618680558310", 5,"【superSAC】您的验证码是{0}，{1}分钟内有效。若非本人操作，请忽略本短信。");
            int i = sendSuperSacCaptcha("1111", "+8618680558310", 5, "【superSAC】Your verification code is {0}. It is valid within {1} minutes. Please ignore the message if it is not your operation.");
            System.out.println(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
