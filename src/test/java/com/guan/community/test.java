package com.guan.community;


import com.guan.community.dao.UserMapper;
import com.guan.community.entity.User;
import com.guan.community.service.UserService;
import com.guan.community.util.CommunityUtil;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class test {
    @Test
    public void Test() {
        System.out.println("test");
    }

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

//    @Test
//    public void modifyPsw() {
//        User user = userMapper.selectByName("inpoch");
//        System.out.println(user);
//        System.out.println(user.getPassword());
//        String temp = "123456";
//        String password = CommunityUtil.MD5(temp + user.getSalt());
//
//        user.setPassword(password);
//        System.out.println(user);
//        System.out.println(user.getPassword());
//
//        String p = "123456";
//        Boolean a = CommunityUtil.MD5(p + user.getSalt()).equals(user.getPassword());
//        System.out.println(a);
//
//    }
}
