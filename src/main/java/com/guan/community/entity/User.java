package com.guan.community.entity;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private String createTime;
}
