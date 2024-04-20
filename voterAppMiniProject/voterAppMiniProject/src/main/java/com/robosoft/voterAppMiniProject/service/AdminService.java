package com.robosoft.voterAppMiniProject.service;


import com.robosoft.voterAppMiniProject.model.*;

import java.util.List;

public interface AdminService {

    String adminRegister(Admin admin);
    int adminSignIn(Long mobNumber,String password);
    String addElections(int sId, Election election);

    List<Election> findAllElections(int sId);
    List<Election> findElectionByName(int sId,String name);
    String updateElectionSchedule(int sId,Election election);
    List<Election> findUpcomingElections(int sId);

    String addWard(int sId, Ward ward);

    List<Ward> findAllWards(int sId);
    List<Ward> findWardByNumber(int sId,int wardNumber);
    String deleteWard(int sId,int wardNumber);


    String addCandidate(int sId, Candidate candidate);
    List<Candidate> findAllCandidates(int sId);
    List<Candidate> findCandidateById(int sId,int cId);
    Boolean deleteCandidate(int sId,int cId);
    String addParties(int sId, Parties parties);

    byte[] getPartyLogo(String partyName);

    String getLogoUrl(String partyName, int sId);

    List<Parties> findExistingParties(int sId);
    List<Parties> findPartyByName(int sId,String name);



    List<Voter> findAllVoters(int sId);
    List<Voter> findVoterByVoterId(int sId,String voterId);


    List<User> findAllUsers(int sId);

    String updatePartyCandidate(int sId,Parties party);

    Election electionResult();

    String winner();
}
