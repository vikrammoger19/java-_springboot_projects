package com.robosoft.voterAppMiniProject.service;


import com.robosoft.voterAppMiniProject.model.*;

import java.util.List;

public interface VoterService {
    String userRegister(User user);
    String userSignIn(Long mobNumber,String password);
    List<Election> findUpcomingElections();


    String castVote(String voterId, Vote vote);

    Election electionResult();

    String voterRegister(Voter voter);
    Voter viewProfile(String voterId);

    byte[] getMyProfilePhoto(String voterId);

    String getImageUrl(String voterId);

    String forgotPassword();


    String winner();

    
}
