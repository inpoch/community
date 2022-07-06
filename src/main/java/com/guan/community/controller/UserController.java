package com.guan.community.controller;


import com.guan.community.annotation.LoginReqired;
import com.guan.community.entity.User;
import com.guan.community.service.UserService;
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

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @LoginReqired
    @GetMapping("/setting")
    public String getSettingPage() {

        return "/site/setting";
    }

    @LoginReqired
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



    @LoginReqired
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
}
