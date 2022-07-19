package com.guan.community.entity;


import java.util.HashMap;
import java.util.Map;

public class Event {
    private String topic;
    //接收对象ID
    private int userId;
    //实体类型（帖子、评论、用户）
    private int entityType;
    //实体ID
    private int entityId;
    //实体的拥有者ID
    private int entityUserId;
    private Map<String, Object> data = new HashMap<>();



    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }


    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public int getUserId() {
        return userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
