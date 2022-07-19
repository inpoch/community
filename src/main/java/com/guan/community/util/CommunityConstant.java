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

    /**
     * 实体类型:帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型:评论
     */
    int ENTITY_TYPE_REPLY = 2;

    /**
     * 实体类型:用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题: 评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题: 点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题: 关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题: 发布帖子
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;

}
