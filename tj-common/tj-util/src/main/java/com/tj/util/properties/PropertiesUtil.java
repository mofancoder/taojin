package com.tj.util.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class PropertiesUtil {
    private static String propertiesUrl = "";
    private static Properties config = new Properties();

    static {
        if (propertiesUrl == null || propertiesUrl.equals(""))
            propertiesUrl = "/config.properties";

        InputStream in = PropertiesUtil.class.getResourceAsStream(propertiesUrl);
        try {
            config.load(in);
        } catch (IOException e) {
            log.error("IOException", e);
            e.printStackTrace();
        }
    }

    public static Set getKeys() {
        return config.keySet();
    }

    public static String getStringValue(String key) {
        return config.getProperty(key);
    }

    public static Integer getIntValue(String key) {
        String s = config.getProperty(key);
        Integer value = Integer.parseInt(s);
        return value;
    }

    public static Long getLongValue(String key) {
        String s = config.getProperty(key);
        Long value = Long.parseLong(s);
        return value;
    }
// public void changeProperties(String url){
//	 InputStream in = getClass().getResourceAsStream(url);
//	 try {
//		config.load(in);
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
// }
/*public static void  main(String arg[]){
	PropertiesUtil p=new PropertiesUtil("");
	String n=(String) p.config.get("invite_idbao");
	System.out.println(n);
}*/
}
