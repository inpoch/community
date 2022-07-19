package com.guan.community.controller;


import com.alibaba.fastjson2.JSONObject;
import com.guan.community.entity.Message;
import com.guan.community.entity.Page;
import com.guan.community.entity.User;
import com.guan.community.service.MessageService;
import com.guan.community.service.UserService;
import com.guan.community.util.CommunityConstant;
import com.guan.community.util.CommunityUtil;
import com.guan.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    public String getMessagePage(Model model, Page page) {
        User user = hostHolder.getUser();

        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setLimit(5);

        List<Message>  conversations = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        if (conversations != null) {
            for (Message conversation : conversations) {
                Map<String, Object> map = new HashMap<>();
                User target = userService.findUserById(conversation.getFromId());
                map.put("target", target);
                map.put("conversation", conversation);
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                map.put("letterCount", messageService.findLetterCount(conversation.getConversationId()));

                maps.add(map);
            }
        }
        model.addAttribute("conversations", maps);

        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null );
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String addLetters(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String letterDetail(@PathVariable String conversationId, Model model, Page page) {

        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail" + conversationId);

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        model.addAttribute("target", getLetterTarget(conversationId));

        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }


        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else return userService.findUserById(id0);
    }

    private List<Integer> getLetterIds(List<Message> messages) {
        List<Integer> list = new ArrayList<>();
        for (Message message : messages) {
            list.add(message.getId());
        }
        return list;
    }

    @GetMapping("/notice/list")
    public String getNoticePage(Model model) {

        User user = hostHolder.getUser();

        //评论
        Message message = messageService.LastNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = new HashMap<>();
        messageVo.put("message", message);
        if (message != null) {
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int unreadCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unreadCount", unreadCount);
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);
        }
        model.addAttribute("commentNotice", messageVo);

        //赞
        message = messageService.LastNotice(user.getId(), TOPIC_LIKE);
        messageVo = new HashMap<>();
        messageVo.put("message", message);
        if (message != null) {
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unreadCount", unread);
        }
        model.addAttribute("likeNotice", messageVo);

        //关注
        message = messageService.LastNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        messageVo.put("message", message);
        if (message != null) {
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unreadCount", unread);
        }
        model.addAttribute("followNotice", messageVo);

        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetailPage(@PathVariable("topic") String topic, Model model, Page page) {

        User user = hostHolder.getUser();

        int rows = messageService.findNoticeCount(user.getId(), topic);
        page.setLimit(10);
        page.setRows(rows);
        page.setPath("/notice/detail/" + topic);

        List<Message> noticeList = messageService.getNoticeList(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> noticeVO = new HashMap<>();
                //通知
                noticeVO.put("notice", notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                noticeVO.put("user", userService.findUserById((Integer) data.get("userId")));
                noticeVO.put("entityType", data.get("entityType"));
                noticeVO.put("entityId", data.get("entityId"));
                noticeVO.put("postId", data.get("postId"));
                //通知作者
                noticeVO.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVOList.add(noticeVO);
            }
        }
        model.addAttribute("notices", noticeVOList);

        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }



        return "/site/notice-detail";
    }
}
