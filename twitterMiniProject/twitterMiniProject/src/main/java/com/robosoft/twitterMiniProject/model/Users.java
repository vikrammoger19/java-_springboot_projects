package com.robosoft.twitterMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class Users {
    private String userId;
    private String userName;
    private String email;
    private String password;
    private long mobNumber;
    private Date dOB;
    private String profile;
    private int followers;
    private int following;
    private String description;
    private String verified;
}
