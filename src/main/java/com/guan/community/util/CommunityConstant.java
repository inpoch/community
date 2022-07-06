package com.guan.community.util;

import org.springframework.stereotype.Component;

@Component
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认有效时间
     */
    int DEFUALT_EXPIRED_SENCONDS = 3600 * 12;

    /**
     * 勾选记住的有效时间
     */
    int REMEMBER_EXPIRED_SENCONDS = 3600 * 24 * 100;
}
