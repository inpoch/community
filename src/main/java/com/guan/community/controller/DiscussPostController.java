package com.guan.community.controller;


import com.guan.community.annotation.LoginRequired;
import com.guan.community.entity.*;
import com.guan.community.event.EventProducer;
import com.guan.community.service.CommentService;
import com.guan.community.service.DiscussPostService;
import com.guan.community.service.LikeService;
import com.guan.community.service.UserService;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.CommunityUtil;
import com.guan.community.util.HostHolder;
import com.guan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/add")
    @LoginRequired
    @ResponseBody
    public String addDiscussPort(String title, String content){

        //判空
        User user = hostHolder.getUser();
        if(user == null) {
            return CommunityUtil.getJSONString(403, "未登录不可发布");
        }

        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model, Page page) {



        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //点赞状态
        String redisKey = RedisKeyUtil.getEntityLikeKey(ENTITY_TYPE_POST, post.getId());
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.likeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);

        //点赞数量
        model.addAttribute("likeCount", likeService.likeCount(ENTITY_TYPE_POST, post.getId()));

        //分页
        page.setLimit(5);
        page.setPath("/add/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论

        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        if (commentList == null) {
            throw new IllegalArgumentException("参数不可为空");
        }
        //评论Vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            Map<String, Object> commentVo = new HashMap<>();
            //评论
            commentVo.put("comment", comment);
            //作者
            commentVo.put("user", userService.findUserById(comment.getUserId()));

            redisKey = RedisKeyUtil.getEntityLikeKey(ENTITY_TYPE_REPLY, comment.getId());
            likeStatus = hostHolder.getUser() == null ? 0 : likeService.likeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_REPLY, comment.getId());
            commentVo.put("likeStatus", likeStatus);
            commentVo.put("likeCount", likeService.likeCount(ENTITY_TYPE_REPLY, comment.getId()));

            //回复列表
            List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_REPLY, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> replyVoList = new ArrayList<>();
            if (replyList != null) {
                //回复Vo
                for (Comment reply : replyList) {
                    Map<String, Object> replyVo = new HashMap<>();
                    replyVo.put("reply", reply);
                    replyVo.put("user", userService.findUserById(reply.getUserId()));

                    User target = reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId());
                    replyVo.put("target",target);

                    redisKey = RedisKeyUtil.getEntityLikeKey(ENTITY_TYPE_REPLY, reply.getId());
                    likeStatus = hostHolder.getUser() == null ? 0 : likeService.likeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_REPLY, reply.getId());
                    replyVo.put("likeStatus", likeStatus);
                    replyVo.put("likeCount", likeService.likeCount(ENTITY_TYPE_REPLY, reply.getId()));

                    replyVoList.add(replyVo);
                }
            }
            commentVo.put("replys", replyVoList);

            int replyCount = commentService.findCountByEntity(ENTITY_TYPE_REPLY, comment.getId());
            commentVo.put("replyCount", replyCount);
            commentVoList.add(commentVo);
        }

        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
