package com.guan.community.service;


import com.guan.community.dao.CommentMapper;
import com.guan.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    public void addComment(Comment comment) {
        commentMapper.insertComment(comment);
    }

    public List<Comment> findCommentsByUser(int userId) {
        return commentMapper.selectCommentsByUserId(userId);
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }




}
