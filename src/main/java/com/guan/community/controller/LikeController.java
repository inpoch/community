package com.guan.community.controller;


import com.guan.community.entity.Event;
import com.guan.community.entity.User;
import com.guan.community.event.EventProducer;
import com.guan.community.service.LikeService;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.CommunityUtil;
import com.guan.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;


    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {

        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //数量
        long likeCount = likeService.likeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.likeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        Event event = new Event()
                .setTopic(TOPIC_LIKE)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityUserId)
                .setData("postId", postId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, null, map);

    }

}
