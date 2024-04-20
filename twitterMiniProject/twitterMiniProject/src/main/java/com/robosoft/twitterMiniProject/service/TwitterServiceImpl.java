package com.robosoft.twitterMiniProject.service;

import com.robosoft.twitterMiniProject.model.Tweets;
import com.robosoft.twitterMiniProject.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service

public class TwitterServiceImpl implements TwitterService{

    @Autowired
    JdbcTemplate jdbcTemplate;

    private int sessionId;


    @Override

    public String signUp(Users users)
    {
        String query="insert into users(userId,userName,email,password,mobNumber,dob,profile,description) values(?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(query,users.getUserId(),users.getUserName(),users.getEmail(),users.getPassword(),users.getMobNumber(),users.getDOB(),users.getProfile(),users.getDescription());
        return "Registered Successfully";
    }
    @Override
    public int signIn(String id, String password)
    {
        try{
        String query="select * from users where userId="+id+ "and password="+password;
        jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Users.class));
        sessionId = new Random().nextInt(1, 1000);
        return sessionId;

    } catch (Exception e) {
        e.printStackTrace();
        return 0;
    }
    }


    @Override
    public List<Tweets> viewAllPosts(int sId) {
        if(sId==sessionId) {
            String query = "select * from tweets";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Tweets.class));
        }
        return null;

    }
    @Override
    public String addPosts(int sId, Tweets tweets)
    {
        if(sId==sessionId)
        {
            String query="insert into tweets values("+tweets.getTweetId()+",'"+tweets.getUserId()+"','"+tweets.getTweetTime()+"','"+tweets.getContent()+"','"+tweets.getAttachment()+"')";
            jdbcTemplate.update(query);
        }
        return "Post added Successfully";
    }
    @Override
    public List<Tweets> editPosts(int sId,int tweetId,Tweets tweets)
    {
        if(sId==sessionId)
        {
            String query="update tweets set tweetId="+tweets.getTweetId()+ "tweetTime="+tweets.getTweetTime()+ "content="+tweets.getContent()+ "attachment="+tweets.getAttachment()+ "where tweetId="+tweetId ;
            return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Tweets.class));


        }
        return null;
    }
    @Override

    public String deleteAllPosts(int sId)
    {
        if(sId==sessionId) {
            String query = "delete from tweets";
            jdbcTemplate.update(query);
            return "deleted all posts";
        }
        return "enter valid session id";
    }
    @Override
    public List<Tweets> deletePostById(int sId,int tweetId)
    {
        if(sId==sessionId)
        {
            String query="delete from tweets where tweetId="+tweetId;
            return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Tweets.class));
        }
        return null;
    }





}
