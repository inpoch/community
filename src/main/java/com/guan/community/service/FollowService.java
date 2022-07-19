package com.guan.community.service;

import com.guan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //关注
    public void follow(int userId, int entityType, int entityId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeRedisKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerRedisKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                operations.opsForZSet().add(followeeRedisKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerRedisKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }


    //取消关注
    public void unFollow(int userId, int entityType, int entityId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeRedisKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerRedisKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                operations.opsForZSet().remove(followeeRedisKey, entityId);
                operations.opsForZSet().remove(followerRedisKey, userId);

                return operations.exec();
            }
        });
    }

    //当前用户是否已关注实体
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followeeRedisKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        boolean hasFollowed = (redisTemplate.opsForZSet().score(followeeRedisKey, entityId) != null);
        return hasFollowed;
    }


    //关注总数
    public Long followeeCount(int userId, int entityType) {
        String followeeRedisKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeRedisKey);
    }

    //粉丝总数
    public Long followerCount(int entityType, int entityId) {
        String followerRedisKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerRedisKey);
    }

    //关注列表
    public List<Map<String, Object>> getFollowee(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followerTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //粉丝列表
    public List<Map<String, Object>> getFollower(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();

            map.put("user", userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followerTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
