package com.guan.community;


import com.guan.community.dao.DiscussPostMapper;
import com.guan.community.dao.elasticsearch.DiscussRepository;
import com.guan.community.entity.DiscussPost;
import com.guan.community.service.DiscussPostService;
import com.guan.community.service.ElasticsearchService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

@SpringBootTest
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private ElasticsearchRepository elasticRepository;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussRepository discussRepository;

    @Test
    public void add() {

        List<DiscussPost> posts = discussMapper.selectDiscussPosts(101, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }

        posts = discussMapper.selectDiscussPosts(102, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(103, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(111, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(112, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(131, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(132, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(133, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        posts = discussMapper.selectDiscussPosts(134, 0, 100);
        if (posts != null) {
            discussRepository.saveAll(posts);
        }
        System.out.println(posts);





    }
}
