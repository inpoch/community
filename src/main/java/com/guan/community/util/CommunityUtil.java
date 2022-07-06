package com.guan.community.util;


import java.util.UUID;

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
}
