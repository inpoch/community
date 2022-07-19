package com.guan.community.controller.Interceptor;


import com.guan.community.entity.User;
import com.guan.community.service.MessageService;
import com.guan.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);

        User u = hostHolder.getUser();
        if (u != null && modelAndView != null) {
            int noticeCount = messageService.findUnreadNoticeCount(u.getId(),null);
            int letterCount = messageService.findLetterUnreadCount(u.getId(),null);
            int unreadCount = noticeCount + letterCount;
        }
    }
}
