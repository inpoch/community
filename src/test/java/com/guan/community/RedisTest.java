package com.guan.community;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void StringTest() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 100);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
    }
}
