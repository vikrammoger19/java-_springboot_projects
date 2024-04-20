package com.robosoft.twitterMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tweets {
    private int tweetId ;
    private String userId ;
    private Date tweetTime ;
    private String content ;
    private String attachment;
}
