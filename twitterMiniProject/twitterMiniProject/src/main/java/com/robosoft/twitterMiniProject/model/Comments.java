package com.robosoft.twitterMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    private int commentId ;
    private int tweetId ;
    private String userId ;
    private String content ;
    private Date time ;
    private byte[] attachment ;
}
