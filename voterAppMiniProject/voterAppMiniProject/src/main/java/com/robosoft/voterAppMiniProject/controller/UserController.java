package com.robosoft.voterAppMiniProject.controller;



import com.robosoft.voterAppMiniProject.model.*;
import com.robosoft.voterAppMiniProject.service.UserService;
import com.robosoft.voterAppMiniProject.service.VoterServiceImpl;
import com.robosoft.voterAppMiniProject.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    VoterServiceImpl voterRegister;
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;



    @Autowired
    private JWTUtility jwtUtility;


    @PostMapping("/authenticate")
    public JWTResponse authenticate(@RequestBody JWTRequest jwtRequest) throws Exception
    {

        try
        {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUserName(),
                            jwtRequest.getPassword()
                    )
            );
        }
        catch (BadCredentialsException e)
        {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = voter.loadUserByUsername(jwtRequest.getUserName());

        final String token = jwtUtility.generateToken(userDetails);

        return  new JWTResponse(token);
    }

    @PostMapping("/userSignUp")

    public ResponseEntity<String> userSignUp(@RequestBody User user) {
        try {
            return new ResponseEntity<>(voterRegister.userRegister(user), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("already registered", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/userSignIn/{mobNumber}/{password}")
    public ResponseEntity<String> userSignIn(@PathVariable Long mobNumber, @PathVariable String password) {

        return new ResponseEntity<>(voterRegister.userSignIn(mobNumber, password), HttpStatus.OK);
    }
    @GetMapping("/Home")
    public ResponseEntity<List<Election>> findUpcomingElections(){
        try {
            return new ResponseEntity<>(voterRegister.findUpcomingElections(), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/RegisterVoter")
    public ResponseEntity<String> voterRegister(@ModelAttribute Voter voter){
        if(voterRegister.voterRegister(voter).equals("duplicate entry")){
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("duplicate entry");
        }
        return new ResponseEntity<>(voterRegister.voterRegister(voter),HttpStatus.CREATED);
    }
    @GetMapping("/viewProfileUrl/{voterId}")
    public ResponseEntity<String> getProfileUrl(@PathVariable String voterId)
    {
        return new ResponseEntity<>(voterRegister.getImageUrl(voterId), HttpStatus.OK);
    }
    @GetMapping("/image/{voterId}")
    public ResponseEntity<Resource> getMyProfilePhoto(@PathVariable String voterId) throws IOException
    {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png")).header("Content-Disposition","filename=\"" + voterId + ".png" +"\"").body(new ByteArrayResource(voterRegister.getMyProfilePhoto(voterId)));
    }
    @GetMapping("/viewProfile/{voterId}")
    public ResponseEntity<Voter> getProfile(@PathVariable String voterId){
        return new ResponseEntity<>(voterRegister.viewProfile(voterId),HttpStatus.OK);
    }
    @PostMapping("/castVote/{voterId}")
    public ResponseEntity<String> castVote(String voterId, Vote vote)
    {
        return ResponseEntity.status(HttpStatus.OK).body(voterRegister.castVote(voterId, vote));
    }
    @GetMapping("/viewElectionResult")
    public ResponseEntity<Election> getElectionResult(){
        return ResponseEntity.status(HttpStatus.OK).body(voterRegister.electionResult());
    }
    @GetMapping("/viewWinner")
    public ResponseEntity<String> getWinner(){
        return ResponseEntity.status(HttpStatus.OK).body(voterRegister.winner());
    }
}
