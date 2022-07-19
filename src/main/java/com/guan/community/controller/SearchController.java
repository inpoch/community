package com.guan.community.controller;


import com.guan.community.entity.DiscussPost;
import com.guan.community.entity.Page;
import com.guan.community.service.CommentService;
import com.guan.community.service.ElasticsearchService;
import com.guan.community.service.LikeService;
import com.guan.community.service.UserService;
import com.guan.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/search")
    public String search(String keyword, Model model, Page page) {

        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        List<Map<String, Object>> posts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.likeCount(ENTITY_TYPE_POST, post.getId()));
                map.put("replyCount", commentService.findCountByEntity(ENTITY_TYPE_POST, post.getId()));

                posts.add(map);
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);

        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
