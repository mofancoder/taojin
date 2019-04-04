package com.tj.util.captcha;

import com.alibaba.fastjson.JSONObject;
import com.tj.util.captcha.ucpaas.AbsRestClient;
import com.tj.util.captcha.ucpaas.JsonReqClient;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UcpaasSendMsg {
    private static String URI_SEND_SMS =
            "https://sms.yunpian.com/v2/sms/single_send.json";
    private static String ENCODING = "UTF-8";
    private static String apikey = "781ce66018d237bd47c6d6422f749ede";

    private static String sid = "96732b247958902858e43b5cbfe3c820";//用户sid
    private static String token = "1eb5ea4d3269bf2ade9664a7b07e8a75";//（鉴权密钥）
    private static String appid = "0a8fdbc2d9b249119fc5649d2e6e756b";
    private static String templateid = "422865";

    static AbsRestClient InstantiationRestAPI() {
        return new JsonReqClient();
    }

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
    public static int sendSuperSacCaptcha(String code, String phone, int time, String message) {
        try {
            String msg = MessageFormat.format(message, code, time);

            String responseText = InstantiationRestAPI().sendSms(sid, token, appid, templateid, msg, phone, "");

            JSONObject json = JSONObject.parseObject(responseText);
            return json.getIntValue("code");
        } catch (Exception e) {
            log.error("", e);
        }
        return -1;
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
//            int i = sendSuperSacCaptcha("1111", "+8618680558310", 5, "【superSAC】Your verification code is {0}. It is valid within {1} minutes. Please ignore the message if it is not your operation.");
//            System.out.println(i);

            int i = sendSuperSacCaptcha("121245", "+8618681443758", 5 * 60, "{0},{1}");
            System.out.println(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
