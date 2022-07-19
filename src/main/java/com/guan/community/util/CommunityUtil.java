package com.guan.community.util;


import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class CommunityUtil {

    //生成随机字符串
    public static String generateUUI() {
        return UUID.randomUUID().toString().replaceAll("_", "");
    }

    //MD5加密
    public static String MD5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //部分刷新
    public static String getJSONString(int code, String msg, Map<String, Object> map) {

        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {

        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code) {

        JSONObject json = new JSONObject();
        json.put("code", code);

        return json.toJSONString();
    }

}
