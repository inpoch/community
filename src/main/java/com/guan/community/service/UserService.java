package com.guan.community.service;

import com.guan.community.dao.LoginTicketMapper;
import com.guan.community.dao.UserMapper;
import com.guan.community.entity.LoginTicket;
import com.guan.community.entity.User;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.CommunityUtil;
import com.guan.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){

        Map<String,Object> map = new HashMap<>();

        //判空
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg","用户名已存在");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg","邮箱已注册");
            return map;
        }

        //注册用户
        String salt = CommunityUtil.generateUUI().substring(0,5);
        user.setSalt(salt);
        user.setPassword(CommunityUtil.MD5(user.getPassword()+salt));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUI());
        user.setHeaderUrl(String.format("http://images.com/head/$dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活账户
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId()+"/"+ user.getActivationCode();
        context.setVariable("url",url);

        String content = templateEngine.process("/mail/activation",context);
        mailClient.mailSend(user.getEmail(),"activation",content);

        return map;
    }

    public int activation(int userId,String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {

        Map<String,Object> map = new HashMap<>();

        //判空
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg","用户名不可为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不可为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg","用户不存在");
            return map;
        }

        if (user.getStatus() == 0) {
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        password = CommunityUtil.MD5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg","密码错误");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUI());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public void updateHeader(int id, String header) {

        //上传头像
        userMapper.updateHeader(id, header);

    }

    public void updatePsw(int id, String password, String salt) {
        userMapper.updatePassword(id, password,salt);
    }

}
