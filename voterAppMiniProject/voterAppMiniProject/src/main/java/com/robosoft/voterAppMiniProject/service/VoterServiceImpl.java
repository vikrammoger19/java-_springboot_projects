package com.robosoft.voterAppMiniProject.service;



import com.robosoft.voterAppMiniProject.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class VoterServiceImpl implements VoterService, UserDetailsService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException
    {
        String emailId=jdbcTemplate.queryForObject("select userName from user where userName=?", String.class, new Object[]{userName});
        String password=jdbcTemplate.queryForObject("select password from user where userName=?",String.class, new Object[]{userName});
        return new org.springframework.security.core.userdetails.User(emailId, password, new ArrayList<>());
    }
    @Override
    public String userRegister(User user) {
        String query="insert into user(userName,mobNumber,password) values('"+user.getUserName()+"',"+user.getMobileNumber()+",'"+user.getPassword()+"')";
        jdbcTemplate.update(query);
        return "user added successfully";
    }

    @Override
    public String userSignIn(Long mobNumber, String password) {
        try {
            String query = "select mobNumber,password from user where mobNumber=" + mobNumber + " and password='" + password + "'";
            jdbcTemplate.queryForObject(query,new BeanPropertyRowMapper<>(KafkaProperties.Admin.class));
            return "signed in";
        } catch (Exception e) {
            return "invalid credentials";
        }

    }

    @Override
    public List<Election> findUpcomingElections() {

            LocalDateTime date = LocalDateTime.now();
            String query="select * from election where startDateAndTime>'"+date+"'  order by startDateAndTime asc " ;
            return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Election.class));
    }

    @Override
    public String castVote(String voterId, Vote vote){
        try {
            String selectQuery = "select voterId from voter where voterId='" + voterId + "'";
            jdbcTemplate.queryForObject(selectQuery, new BeanPropertyRowMapper<>(Voter.class));
            String candidate = "select partyName,logoUrl,candidateId,wardNumber from parties where partyName='" + vote.getPartyName() + "','" + vote.getLogoUrl() + "'," + vote.getCandidateId() + "," + vote.getWardNumber() + "";
            jdbcTemplate.query(candidate, new BeanPropertyRowMapper<>(Parties.class));
            String election = "select electionName from election where electionName='" + vote.getElectionName() + "'";
            jdbcTemplate.queryForObject(election, new BeanPropertyRowMapper<>(Election.class));

            String query = "insert into vote values(" + vote.getWardNumber() + ",'" + vote.getElectionName() + "','" + vote.getPartyName() + "','" + vote.getLogoUrl() + "'," + vote.getCandidateId() + ")";
            jdbcTemplate.update(query);
            String voteCount="select voteCount from candidate where candidateId="+vote.getCandidateId();
            String updateQuery="update candidate set voteCount=? where candidateId="+vote.getCandidateId();
            jdbcTemplate.update(updateQuery,voteCount+1);
            return "vote casted";
        }catch (Exception e){
            return "enter valid credentials";
        }
    }

    @Override
    public Election electionResult()
    {
        try {
            String query = "select electionPartyMapping.electionName,electionPartyMapping.partyName,electionPartyMapping.voteCount,parties.wardNumber,electionPartyMapping.candidateId,election.startDateAndTime,election.endDateAndTime from electionPartyMapping inner join parties on electionPartyMapping.partyName=parties.partyName inner join election on election.electionName=electionPartyMapping.electionName ";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Election.class));
        }catch (Exception e)
        {
            System.out.println("election name does not exist");
            return null;
        }
    }

    @Override
    public String voterRegister(Voter voter)
    {
        String fileName= StringUtils.cleanPath(voter.getProfileImage().getOriginalFilename());
        String downloadUrl;
        try {
            String seQuery = "select userId from user where userId=" + voter.getUserId()+"";
            jdbcTemplate.queryForObject(seQuery,new BeanPropertyRowMapper<>(User.class));
            if (voter.getAge() < 18) {
                System.out.println("age is below 18 ");
            }
            try {
                if (fileName.contains("..")) {
                    throw new Exception("file name is invalid" + fileName);
                }
                downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/RegisterVoter/")
                        .path(voter.getName())
                        .toUriString();
                String finalDownloadUrl = downloadUrl;
                String query = "insert into voter(userId,voterId,name,gender,age,wardNumber,aadharNumber,mobNumber,address,profileImage,profileUrl) values(?,?,?,?,?,?,?,?,?,?,?)";
                jdbcTemplate.update(query,voter.getUserId(),voter.getVoterId(),voter.getName(),voter.getGender(),voter.getAge(),voter.getWardNumber(),voter.getAadharNumber(),voter.getMobNumber(),voter.getAddress(),voter.getProfileImage().getBytes(),finalDownloadUrl);
            } catch (Exception e) {
                e.printStackTrace();


            }
        }catch (Exception e)
        {
            System.out.println("could not save file" + fileName);
            return "duplicate entry";
        }
        return "successfully registered";
    }
    @Override
    public byte[] getMyProfilePhoto(String voterId)
    {
        String get_image = "select profilePhoto from voter where voterId='" + voterId + "'";
        return jdbcTemplate.queryForObject(get_image,byte[].class);
    }
    @Override
    public String getImageUrl(String voterId) {
        try {
            String url = "select profileUrl from voter where voterId='" + voterId + "'";
            return jdbcTemplate.queryForObject(url, String.class);
        } catch (Exception e) {
            return "Incorrect SessionId";
        }
    }


    @Override
    public String forgotPassword() {
        return null;
    }

    @Override
    public Voter viewProfile(String voterId) {
        try {
            String query = "select userId,voterId,name,gender,age,wardNumber,aadharNumber,mobNumber,address,profileUrl from voter where voterId='" + voterId + "'";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Voter.class));
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("voter id does not found");
            return null;
        }
    }




    @Override
    public String winner() {
        try {
            String query = "select electionName,partyName,candidateId,max(voteCount) from electionPartyMapping";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(String.class));
        }catch (Exception e) {
            return null;
        }
    }
}
