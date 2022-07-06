package com.guan.community.util;

import com.guan.community.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;

@Component
public class HostHolder {
    private final ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return  users.get();
    }

    public void clear() {
        users.remove();
    }
}
