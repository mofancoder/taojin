
package com.tj.util.captcha.ucpaas;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class JsonReqClient extends AbsRestClient {
	
	@Override
	public String sendSms(String sid, String token, String appid, String templateid, String param, String mobile, 
			String uid) {
		
		String result = "";
		
		try {
			String url = getStringBuffer().append("/sendsms").toString();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("param", param);
			jsonObject.put("mobile", mobile);
			jsonObject.put("uid", uid);
			
			String body = jsonObject.toJSONString();
			
			result = postJson(url, body, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String sendSmsBatch(String sid, String token, String appid, String templateid, String param, String mobile,
			String uid) {
		
		String result = "";
		
		try {
			String url = getStringBuffer().append("/sendsms_batch").toString();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("param", param);
			jsonObject.put("mobile", mobile);
			jsonObject.put("uid", uid);
			
			String body = jsonObject.toJSONString();
			
			result = postJson(url, body, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String addSmsTemplate(String sid, String token, String appid, String type, String template_name,
			String autograph, String content) {
		
		String result = "";
		
		try {
			String url = getStringBuffer().append("/addsmstemplate").toString();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("type", type);
			jsonObject.put("template_name", template_name);
			jsonObject.put("autograph", autograph);
			jsonObject.put("content", content);
			
			String body = jsonObject.toJSONString();

			result = postJson(url, body, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getSmsTemplate(String sid, String token, String appid, String templateid, String page_num,
			String page_size) {
		
		String result = "";
		
		try {
			String url = getStringBuffer().append("/getsmstemplate").toString();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("page_num", page_num);
			jsonObject.put("page_size", page_size);
			
			String body = jsonObject.toJSONString();
			
			result = postJson(url, body, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String editSmsTemplate(String sid, String token, String appid, String templateid, String type,
			String template_name, String autograph, String content) {
		
		String result = "";
		
		try {
			String url = getStringBuffer().append("/editsmstemplate").toString();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("type", type);
			jsonObject.put("template_name", template_name);
			jsonObject.put("autograph", autograph);
			jsonObject.put("content", content);
			
			String body = jsonObject.toJSONString();
			
			result = postJson(url, body, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String deleterSmsTemplate(String sid, String token, String appid, String templateid) {

		String result = "";
		
		try {
			String url = getStringBuffer().append("/deletesmstemplate").toString();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			
			String body = jsonObject.toJSONString();
			result = postJson(url, body, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public static String postJson(String url, String body, String charset) {

		String result = null;

		if (null == charset) {
			charset = "UTF-8";
		}
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		try {
			httpClient = HttpConnectionManager.getInstance().getHttpClient();
			httpPost = new HttpPost(url);

			// 设置连接超时,设置读取超时
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(10000)
					.setSocketTimeout(10000)
					.build();
			httpPost.setConfig(requestConfig);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json;charset=utf-8");

			// 设置参数
			StringEntity se = new StringEntity(body, "UTF-8");
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
}

