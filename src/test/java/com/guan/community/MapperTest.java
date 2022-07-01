package com.guan.community;

import com.guan.community.dao.DiscussPostMapper;
import com.guan.community.dao.UserMapper;
import com.guan.community.entity.DiscussPost;
import com.guan.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MapperTest {
    @Autowired
    UserMapper userMapper;
    @Test
    public void UserMapperTest(){
        User user = userMapper.selectById(101);
        System.out.println(user.getHeaderUrl());
        user = userMapper.selectByName("liubei");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
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
}
