package com.guan.community.dao;

import com.guan.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
    List<Message> selectByToId (@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询当前用户的会话数量.
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表.
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询某个会话所包含的私信数量.
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    int selectLetterUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    int insertLetter(Message message);

    //设置已读
    int updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    // 查询某个话题的会话数量.
    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    //查询未读通知数量
    int selectUnreadNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    //查询某话题最近通知
    Message selectLastNotice(@Param("userId") int userId, @Param("topic") String topic);

    //查询某话题的通知列表
    List<Message> selectNotices(@Param("userId") int userId, @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);
}
