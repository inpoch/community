package com.guan.community.controller;


import com.guan.community.annotation.LoginRequired;
import com.guan.community.entity.Comment;
import com.guan.community.entity.DiscussPost;
import com.guan.community.entity.Page;
import com.guan.community.entity.User;
import com.guan.community.service.*;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.CommunityUtil;
import com.guan.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {

        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/uploadHeader")
    public String uploadHeader(MultipartFile headerImg, Model model) {

        if (headerImg == null) {
            model.addAttribute("error","您还没选择图片");
            return "/site/setting";
        }


        String fileName = headerImg.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "图片格式错误");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUI() + suffix;
        File dest = new File(uploadPath + "/" + fileName);

        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常");
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }



    @LoginRequired
    @PostMapping("/uploadPsw")
    public String uploadPsw(Model model, String oldPsw, String newPsw) {

        //判空
        if (oldPsw == null) {
            model.addAttribute("oldPswError", "请输入原密码");
            return "/site/setting";
        }

        if (newPsw == null) {
            model.addAttribute("newPswError", "请输入新密码");
            return "/site/setting";
        }

        //判断密码正确性
        User user = hostHolder.getUser();
        oldPsw = CommunityUtil.MD5(oldPsw + user.getSalt());
        if (!user.getPassword().equals(oldPsw)) {
            model.addAttribute("oldPswError", "密码不正确");
            return "/site/setting";
        }

        //判断相同
        if (user.getPassword().equals(CommunityUtil.MD5(newPsw + user.getSalt()))) {
            model.addAttribute("newPswError", "新密码不可与旧密码相同");
            return "/site/setting";
        }

        String salt = CommunityUtil.generateUUI().substring(0,5);
        newPsw = CommunityUtil.MD5(newPsw + salt);
        userService.updatePsw(user.getId(), newPsw, salt);

        return "redirect:/index";
    }


    //个人主页
    @GetMapping("/profile/{userId}")
    public String getMainPage(Model model, @PathVariable("userId") int userId) {

        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //关注的总数
        long followeeCount = followService.followeeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝总数
        long followerCount = followService.followerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //用户收到的赞
        long likeCount = likeService.findEntityLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        //是否已关注
        Boolean hasFollowed = followService.isFollower(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }

    @GetMapping("/myPost/{userId}")
    public String myPost(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);

        page.setLimit(5);
        page.setPath("/user/myPost/{userId}");
        int count = discussPostService.findDiscussPostRows(userId);
        page.setRows(count);

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(user.getId(),page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        if (discussPosts != null) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                Long likeCount = likeService.likeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("post", discussPost);
                map.put("likeCount", likeCount);
                list.add(map);
            }
        }


        model.addAttribute("posts", list);
        model.addAttribute("postCount", count);
        model.addAttribute("user", user);


        return "/site/my-post";
    }

    @GetMapping("/myReply/{userId}")
    public String myReply(@PathVariable("userId") int userId, Model model, Page page) {

        User user = userService.findUserById(userId);
        List<Comment> comments = commentService.findCommentsByUser(userId);

        page.setPath("/user/myReply/{userId}");
        page.setLimit(5);
        page.setRows(comments.size());

        List<Map<String, Object>> list = new ArrayList<>();
        for (Comment comment:comments) {
            Map<String, Object> map = new HashMap<>();
            DiscussPost post;
            if (comment.getEntityType() == 1) {
                post = discussPostService.findDiscussPostById(comment.getEntityId());
                map.put("post", post);
            }
            map.put("comment", comment);
            list.add(map);
        }


        model.addAttribute("maps",list);
        model.addAttribute("count", comments.size());
        model.addAttribute("user", user);

        return "/site/my-reply";

    }
}
