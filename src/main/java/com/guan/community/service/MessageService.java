package com.guan.community.service;


import com.guan.community.dao.MessageMapper;
import com.guan.community.entity.Message;
import com.guan.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectByToId(userId, offset, limit);
    }

    // 查询当前用户的会话数量.
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    // 查询某个会话所包含的私信列表.
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    // 查询某个会话所包含的私信数量.
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    // 查询未读私信的数量
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public void addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        messageMapper.insertLetter(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    // 查询某个话题的会话数量.
//    int selectNoticeCount(int userId, String topic);
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }
    //查询未读通知数量
//    int selectUnreadNoticeCount(int userId, String topic);
    public int findUnreadNoticeCount(int userId, String topic) {
        return messageMapper.selectUnreadNoticeCount(userId, topic);
    }
//    //查询某话题最近通知
//    Message selectLastNotice(int userId, String topic);
    public Message LastNotice(int userId, String topic) {
        return messageMapper.selectLastNotice(userId, topic);
    }

//    //查询某话题的通知列表
//    List<Message> selectNotices(int userId, String topic, int offset, int limit);
    public List<Message> getNoticeList(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
