package com.robosoft.twitterMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashTags {
    private String hashTagName ;
    private int tweetId ;
}
