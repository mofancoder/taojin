package com.tj.util;

import com.tj.util.http.Https;
import com.tj.util.properties.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PanZhiSendMsg {
    private static String URI_SEND_SMS = "http://api-dx.panzhi.net/http1.php";
    private static String password = PropertiesUtil.getStringValue("panzhi.password");

    public static int sendSms(String text,
                              String mobile, String sign) {
        text = text + sign;
        log.debug("|panzhi send Msg mobile:" + mobile + " text:" + text);
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "send");
        params.put("orgid", "175");
        params.put("username", "sacbox");
        params.put("passwd", password);
        params.put("destnumbers", mobile);
        params.put("msg", text);
        try {
            String resp = Https.post(URI_SEND_SMS, params);

            log.debug("|panzhi send resp:" + resp);
            if (resp.contains("&")) {
                String status = resp.substring(0, resp.indexOf("&"));
                if (status.contains("state=0")) {
                    return 0;
                }
            }
            return -1;
        } catch (Exception e) {
            log.error("panzhi send Msg Exception ", e);
        }
        return -1;
    }

    public static void main(String[] args) {

        String msg = "提币服务器链接异常1，请及时查看";
        // msg =  URLEncoder.encode(msg);
        int resp = sendSms(msg, "13557832237", "【SAC监控中心】");
        System.out.println(resp);

    }
}
