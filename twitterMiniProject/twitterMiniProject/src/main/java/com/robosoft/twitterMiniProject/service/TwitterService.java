package com.robosoft.twitterMiniProject.service;

import com.robosoft.twitterMiniProject.model.Tweets;
import com.robosoft.twitterMiniProject.model.Users;

import java.util.List;

public interface TwitterService {
     String signUp(Users users);

    int signIn(String id, String password);



    List<Tweets> viewAllPosts(int sessionId);



     String addPosts(int sId,Tweets tweets);

     List<Tweets> editPosts(int sId,int tweetId,Tweets tweets);

     String deleteAllPosts(int sId);
     List<Tweets> deletePostById(int sId,int tweetId);

}
