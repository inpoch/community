package com.guan.community.controller;

import com.guan.community.entity.DiscussPost;
import com.guan.community.entity.Page;
import com.guan.community.entity.User;
import com.guan.community.service.DiscussPostService;
import com.guan.community.service.LikeService;
import com.guan.community.service.MessageService;
import com.guan.community.service.UserService;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.HostHolder;
import com.guan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;



    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");




        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                map.put("likeCount", likeService.likeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);

            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return  "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }
}
