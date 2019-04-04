package com.tj.util.captcha;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tj.util.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CertificateUtil {

    /*
     * 获取参数的json对象
     */
    public static JSONObject getParam(int type, String dataValue) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("dataType", type);
            obj.put("dataValue", dataValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String ocrGeneralOfJuhe(String phone, String Name, String cardNo) {
        String host = "http://telecom-ali.juheapi.com";
        String path = "/telecom/query";
        String method = "GET";
        String appcode = "f4f8883625594a189d34308698201d52";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("idcard", cardNo);
        querys.put("realname", Name);
        querys.put("mobile", phone);

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());
            //获取response的body
            if (response.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(response.getEntity());

                JSONObject json = JSONObject.parseObject(str);
                String errorCode = json.getString("error_code");

                if (errorCode.equals("0")) {        //身份证及姓名合法
                    JSONObject result = json.getJSONObject("result");
                    String res = result.getString("res");
                    if (!res.equals("1")) {
                        String resmsg = result.getString("resmsg");
                        return resmsg;
                    } else {
                        return null;
                    }

                } else {
                    String reason = json.getString("reason");
                    return reason;
                }
            } else {
                return "审核异常";
            }
        } catch (Exception e) {
            log.error("", e);

        }
        return "正在查询校验中";
    }

    public static void main(String[] args) throws IOException {
        //Https.get(url)
        //		String imgFile="D:\\7.jpg";
        //		File file = new File(imgFile);
        //        byte[] content = new byte[(int) file.length()];
        //        FileInputStream finputstream = new FileInputStream(file);
        //        finputstream.read(content);
        //        finputstream.close();
        //		JSONObject json = CertificatePic(content,"back");
        //		System.out.println(json.toJSONString());
        System.out.println(ocrGeneralOfJuhe("15364995158", "李晓东", "172731199101135411"));

    }
}
