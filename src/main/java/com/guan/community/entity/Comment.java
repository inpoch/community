package com.guan.community.entity;


import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private int id;
    private int userId;
    //在对应实体类型中的id
    private int entityId;
    //实体类型id（帖子|评论）
    private int entityType;
    //回复大的对象的id
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
