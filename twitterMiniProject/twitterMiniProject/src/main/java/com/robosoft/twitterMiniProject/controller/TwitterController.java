package com.robosoft.twitterMiniProject.controller;

import com.robosoft.twitterMiniProject.model.Tweets;
import com.robosoft.twitterMiniProject.model.Users;
import com.robosoft.twitterMiniProject.service.TwitterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TwitterController {
    @Autowired
    TwitterServiceImpl twitterService;

    @PostMapping("/signUp")
    public String signUp(@RequestBody Users users) {
        return twitterService.signUp(users);
    }

    @PostMapping("/signIn")
    public int signIn(@PathVariable String id, @PathVariable String password) {
        return twitterService.signIn(id, password);
    }


    @GetMapping("/{sId}/viewAllPosts")
    public List<Tweets> viewAllPosts(int sId) {
        return twitterService.viewAllPosts(sId);
    }

    @PostMapping("/{sId}/addPosts")
    public String addPosts(@PathVariable int sId, @RequestBody Tweets tweets) {
        return twitterService.addPosts(sId, tweets);
    }

    @PutMapping("/{sId}/editPosts")
    public List<Tweets> editPosts(@PathVariable int sId, @PathVariable int tweetId, @RequestBody Tweets tweets) {
        return twitterService.editPosts(sId, tweetId, tweets);
    }

    @DeleteMapping("/{sId}/deleteAllPosts")
    String deleteAllPosts(@PathVariable int sId) {
        return twitterService.deleteAllPosts(sId);
    }

    @DeleteMapping("/{sId}/deletePostById/{tweetId}")
    List<Tweets> deletePostById(@PathVariable int sId, @PathVariable int tweetId) {
        return twitterService.deletePostById(sId, tweetId);
    }
}
