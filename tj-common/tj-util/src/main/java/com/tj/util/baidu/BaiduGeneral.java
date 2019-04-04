package com.tj.util.baidu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import com.tj.util.http.Https;
import com.tj.util.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class BaiduGeneral {
    //设置APPID/AK/SK
    public static final String APP_ID = "11360805";
    public static final String API_KEY = "6Pifr1WZVYNPCSY6WZn3pfDK";
    public static final String SECRET_KEY = "xHK8T9bcStkfVUyfhND2LyOc9OVz3FX8";
    public static final AipOcr client;

    static {
        client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
    }

    public static JSONObject BaiduCertificatePic(byte[] src, String side) {
        // 初始化一个AipOcr


        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        // System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        options.put("detect_risk", "false");

        String idCardSide = side;

        org.json.JSONObject res = null;
        res = client.idcard(src, idCardSide, options);
        String jsonstr;
        try {
            jsonstr = res.toString(2);
            JSONObject json = JSONObject.parseObject(jsonstr);
            return json;

        } catch (JSONException e) {
            log.error("", e);
        }

        return null;
    }

    /**
     * 身份证反面
     *
     * @param src
     * @return
     */
    public static JSONObject BaiduCertificateFrontPic(byte[] src) {

        JSONObject result = new JSONObject();
        try {

            JSONObject json = BaiduCertificatePic(src, "front");
            if (json == null) return result;
            if (json.containsKey("words_result")) {
                JSONObject words_result = json.getJSONObject("words_result");
                if (words_result.containsKey("姓名")) {
                    JSONObject namejson = words_result.getJSONObject("姓名");
                    result.put("name", namejson.getString("words"));
                }
                if (words_result.containsKey("公民身份号码")) {
                    JSONObject cardNojson = words_result.getJSONObject("公民身份号码");
                    result.put("cardNo", cardNojson.getString("words"));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("", e);
            return result;
        }
    }

    public static boolean BaiduCertificateBackPic(byte[] src) {
        try {
            JSONObject json = BaiduCertificatePic(src, "back");
            log.error("BaiduCertificateBackPic:" + JSON.toJSONString(json));
            if (json.containsKey("words_result")) {
                JSONObject words_result = json.getJSONObject("words_result");
                if (words_result.containsKey("失效日期")) {
                    JSONObject timejson = words_result.getJSONObject("失效日期");
                    String timeStr = timejson.getString("words");
                    if (timeStr.contains("长期")) {
                        return true;
                    }
                    Date endDate = TimeUtil.getDateFormat(timeStr, "yyyyMMdd");
                    Date now = new Date();
                    if (now.getTime() < endDate.getTime()) {
                        return true;
                    }
                }

            }
            return false;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }

    }

    public static void main(String[] args) throws IOException {
//		byte[] src = Https.getImage("https://gdpic.oss-cn-shenzhen.aliyuncs.com/userInfo/c1c9b6b9f142561f1b55bca444eb72ae.png");
//		BaiduGeneral.BaiduCertificateFrontPic(src);
        byte[] back = Https.getImage("https://gdpic.oss-cn-shenzhen.aliyuncs.com/userInfo/ec884b8a99432a2a51c607daeb5fc850.png");
        BaiduGeneral.BaiduCertificateBackPic(back);
    }
}
