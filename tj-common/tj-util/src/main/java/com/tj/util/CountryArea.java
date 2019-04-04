package com.tj.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CountryArea {
    public List<Map> EnglishPhonePrefix() {
        List<Map> prefixList = new ArrayList<>();
        Map item = new HashMap();
        item.put("name", "China");
        item.put("prefix", "+86");
        Map item2 = new HashMap();
        item2.put("name", "HongKong");
        item2.put("prefix", "+852");
        Map item3 = new HashMap();
        item3.put("name", "Macao");
        item3.put("prefix", "+853");
        Map item4 = new HashMap();
        item4.put("name", "Taiwan");
        item4.put("prefix", "+886");
        Map item5 = new HashMap();
        item5.put("name", "Japan");
        item5.put("prefix", "+8801");
        Map item6 = new HashMap();
        item6.put("name", "Korea");
        item6.put("prefix", "+8802");
        Map item7 = new HashMap();
        item7.put("name", "America");
        item7.put("prefix", "+001");
        Map item8 = new HashMap();
        item8.put("name", "Britain");
        item8.put("prefix", "+44");
        Map item9 = new HashMap();
        item9.put("name", "Australia");
        item9.put("prefix", "+0061");


        prefixList.add(item);
        prefixList.add(item2);
        prefixList.add(item3);
        prefixList.add(item4);
        prefixList.add(item5);
        prefixList.add(item6);
        prefixList.add(item7);
        prefixList.add(item8);
        prefixList.add(item9);


        return prefixList;
    }

    public List<Map> ChinaPhonePrefix() {
        List<Map> prefixList = new ArrayList<>();
        Map item = new HashMap();
        item.put("name", "中国");
        item.put("prefix", "+86");
        Map item2 = new HashMap();
        item2.put("name", "中国香港");
        item2.put("prefix", "+852");
        Map item3 = new HashMap();
        item3.put("name", "中国澳门");
        item3.put("prefix", "+853");
        Map item4 = new HashMap();
        item4.put("name", "中国台湾");
        item4.put("prefix", "+886");
        Map item5 = new HashMap();
        item5.put("name", "日本");
        item5.put("prefix", "+8801");
        Map item6 = new HashMap();
        item6.put("name", "韩国");
        item6.put("prefix", "+8802");
        Map item7 = new HashMap();
        item7.put("name", "美国");
        item7.put("prefix", "+001");
        Map item8 = new HashMap();
        item8.put("name", "英国");
        item8.put("prefix", "+44");
        Map item9 = new HashMap();
        item9.put("name", "澳大利亚");
        item9.put("prefix", "+0061");

        prefixList.add(item);
        prefixList.add(item2);
        prefixList.add(item3);
        prefixList.add(item4);
        prefixList.add(item5);
        prefixList.add(item6);
        prefixList.add(item7);
        prefixList.add(item8);
        prefixList.add(item9);

        return prefixList;
    }


    public String getNation(String prefix) {
        List<Map> maps = EnglishPhonePrefix();
        for (Map map : maps) {
            if (map.get("prefix").equals(prefix)) {
                return (String) map.get("name");
            }
        }
        return "";
    }
}
