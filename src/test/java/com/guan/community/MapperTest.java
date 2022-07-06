package com.guan.community;

import com.guan.community.dao.DiscussPostMapper;
import com.guan.community.dao.LoginTicketMapper;
import com.guan.community.dao.UserMapper;
import com.guan.community.entity.DiscussPost;
import com.guan.community.entity.LoginTicket;
import com.guan.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MapperTest {
    @Autowired
    UserMapper userMapper;
    @Test
    public void UserMapperTest(){
//        User user = userMapper.selectById(101);
//        System.out.println(user.getHeaderUrl());
//        user = userMapper.selectByName("liubei");
//        System.out.println(user);
//        user = userMapper.selectByEmail("nowcoder101@sina.com");
//        System.out.println(user);
        System.out.println(userMapper.selectByEmail("gcw@stu.ahau.edu.cn"));
    }

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void DiscussMapperTest(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,1,10);

        for (DiscussPost post : list){
            System.out.println(post);
//            System.out.println(userMapper.selectById(post.getId()).getHeaderUrl());
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void LoginTicketMapperTest() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setTicket("gjyt6tfv");
        Date date = new Date();
        loginTicket.setExpired(date);

        loginTicketMapper.insertLoginTicket(loginTicket);
        loginTicket = loginTicketMapper.selectByTicket(loginTicket.getTicket());
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus(loginTicket.getTicket(),1);
    }
}
