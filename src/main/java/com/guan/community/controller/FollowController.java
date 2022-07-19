package com.guan.community.controller;


import com.guan.community.entity.Event;
import com.guan.community.entity.Page;
import com.guan.community.entity.User;
import com.guan.community.event.EventProducer;
import com.guan.community.service.FollowService;
import com.guan.community.service.UserService;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.CommunityUtil;
import com.guan.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    //关注
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {

        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "成功关注", null);
    }

    //取消关注
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {

        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "成功取消关注", null);
    }

    //关注用户列表
    @GetMapping("/followee/{userId}")
    public String followeeList(@PathVariable("userId") int userId, Model model, Page page) {

        //分页
        page.setPath("/followee" + userId);
        long followeeCount = followService.followeeCount(userId, ENTITY_TYPE_USER);
        page.setRows((int)followeeCount);
        page.setLimit(5);

        List<Map<String, Object>> list = followService.getFollowee(userId, ENTITY_TYPE_USER, page.getOffset(), page.getLimit());
        if (list != null) {
            model.addAttribute("followeeList", list);
        }
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        //是否已关注
        Boolean hasFollowed = followService.isFollower(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/followee";
    }


    //粉丝列表
    @GetMapping("/follower/{userId}")
    public String followerList(@PathVariable("userId") int userId, Model model, Page page) {
        //分页
        page.setPath("/follower/" + userId);
        long followerCount = followService.followerCount(ENTITY_TYPE_USER, userId);
        page.setRows((int)followerCount);
        page.setLimit(5);

        List<Map<String, Object>> list = followService.getFollower(ENTITY_TYPE_USER, userId, page.getOffset(), page.getLimit());
        if (list != null) {
            model.addAttribute("followerList", list);
        }
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        //是否已关注
        Boolean hasFollowed = followService.isFollower(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/follower";
    }

}
