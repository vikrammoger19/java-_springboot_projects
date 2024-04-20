package com.robosoft.voterAppMiniProject.service;


import com.robosoft.voterAppMiniProject.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private int sessionId;

    @Override
    public String adminRegister(Admin admin) {

        String query = "insert into admin (adminName,email,mobNumber,password) values('" + admin.getAdminName() + "','" + admin.getEmail() + "'," + admin.getMobNumber() + ",'" + admin.getPassword() + "')";
        jdbcTemplate.update(query);
        return "Admin Registered Successfully";
    }

    @Override
    public int adminSignIn(Long mobNumber, String password) {
        try {
            String query = "select mobNumber,password from admin where mobNumber=" + mobNumber + " and password='" + password + "'";
            jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Admin.class));
            sessionId = new Random().nextInt(1, 1000);
            return sessionId;
        } catch (Exception e) {
            System.out.println("invalid credentials");
            return 0;

        }
    }

    @Override
    public String addElections(int sId, Election election) {

        if (sId == sessionId) {
            try {
                String query = "insert into election values('" + election.getElectionName() + "','" + election.getStartDateAndTime() + "','" + election.getEndDateAndTime() + "')";
                jdbcTemplate.update(query);
                return "Election Added";
            } catch (Exception e) {
                return "duplicate entry";
            }
        }
        return "enter valid session id";

    }

    @Override
    public List<Election> findAllElections(int sId) {
        if (sId == sessionId) {
            String query = "select * from election";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Election.class));
        }
        return null;
    }

    @Override
    public List<Election> findElectionByName(int sId, String name) {
        if (sId == sessionId) {
            String query = "select * from election where electionName='" + name + "'";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Election.class));
        }
        return null;
    }

    @Override
    public String updateElectionSchedule(int sId, Election election) {
        if (sId == sessionId) {
            try {

                String query = "update election set startDateAndTime ='" + election.getStartDateAndTime() + "',endDateAndTime='" + election.getEndDateAndTime() + "' where electionName='" + election.getElectionName() + "'";
                jdbcTemplate.update(query);
                return "Election Rescheduled";
            } catch (Exception e) {
                return "enter correct election name";
            }
        }
        return "enter valid session id";
    }

    @Override
    public List<Election> findUpcomingElections(int sId) {
        if (sId == sessionId) {
            LocalDateTime date = LocalDateTime.now();
            String query = "select * from election where startDateAndTime>'" + date + "'  order by startDateAndTime asc ";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Election.class));
        }
        return null;
    }


//    public Boolean authentication(int sId)
//    {
//        if(sId==sessionId)
//        {
//            return true;
//        }
//        return false;
//    }

    @Override
    public String addWard(int sId, Ward ward) {
        if (sId == sessionId) {
            try {
                String query = "insert into ward values(" + ward.getWardNumber() + ",'" + ward.getWardName() + "')";
                jdbcTemplate.update(query);
                return "ward Added";
            } catch (Exception e) {
                return "duplicate entry";
            }
        }
        return "enter valid session id";

    }

    @Override
    public List<Ward> findAllWards(int sId) {
        if (sId == sessionId) {
            String query = "select * from ward";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Ward.class));
        }
        return null;
    }

    @Override
    public List<Ward> findWardByNumber(int sId, int wardNumber) {
        if (sId == sessionId) {
            String query = "select * from ward where wardNumber=" + wardNumber;
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Ward.class));
        }
        return null;
    }

    @Override
    public String deleteWard(int sId, int wardNumber) {
        if (sId == sessionId) {
            try {
                String query = "delete from ward where wardNumber=" + wardNumber;
                jdbcTemplate.update(query);
                return "ward removed successfully";
            } catch (Exception e) {
                return "ward number does not exist";
            }
        }
        return "enter valid session id";
    }

    @Override
    public String addCandidate(int sId, Candidate candidate) {
        try {
            String selectQuery = "select voterId,name from voter where voterId='" + candidate.getVoterId() + "'and name='" + candidate.getName() + "'";
            jdbcTemplate.queryForObject(selectQuery, new BeanPropertyRowMapper<>(Voter.class));
            String query = "insert into candidate (voterId,name) values(?,?)";
            jdbcTemplate.update(query, candidate.getVoterId(), candidate.getName());
            return "candidate added";
        } catch (Exception e) {
            e.printStackTrace();
            return "enter valid voter id";
        }
    }

    @Override
    public List<Candidate> findAllCandidates(int sId) {
        if (sId == sessionId) {

            String query = "select * from candidate";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Candidate.class));
        }
        return null;
    }

    @Override
    public List<Candidate> findCandidateById(int sId, int cId) {
        if (sId == sessionId) {
            String query = "select * from candidate where candidateId=" + cId;
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Candidate.class));
        }
        return null;
    }

    @Override
    public Boolean deleteCandidate(int sId, int cId) {
        if (sId == sessionId) {
            try {
                String query = "delete from candidate where candidateId=" + cId;
                jdbcTemplate.update(query);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String addParties(int sId, Parties parties) {
        if (sId == sessionId) {
            String fileName = StringUtils.cleanPath(parties.getLogo().getOriginalFilename());
            String downloadUrl;
            try {
                String seQuery = "select candidateId,voterId from candidate where candidateId=" + parties.getCandidateId() + "";
                jdbcTemplate.queryForObject(seQuery, new BeanPropertyRowMapper<>(Candidate.class));
                if (fileName.contains("..")) {
                    throw new Exception("file name is invalid" + fileName);
                }
                downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/addParties/")
                        .path(parties.getPartyName())
                        .toUriString();
                String finalDownloadUrl = downloadUrl;
                String query = "insert into Parties(partyName,logo,logoUrl,candidateId,wardNumber,voterId) values(?,?,?,?,?,?)";
                jdbcTemplate.update(query, parties.getPartyName(), parties.getLogo().getBytes(), finalDownloadUrl, parties.getCandidateId(), parties.getWardNumber(), parties.getVoterId());
            } catch (Exception e) {
                System.out.println("could not save file" + fileName);
                e.printStackTrace();

            }
            return "Party already exist";
        }
        return "invalid session id";
    }
    @Override
    public byte[] getPartyLogo(String partyName)
    {
        String get_image = "select logo from parties where partyName='" + partyName + "'";
        return jdbcTemplate.queryForObject(get_image,byte[].class);
    }
    @Override
    public String getLogoUrl(String partyName, int sId)
    {
        if(sId == sessionId)
        {
            try {
                String url = "select logoUrl from  parties where partyName='" + partyName + "'";
                return jdbcTemplate.queryForObject(url, String.class);
            }
            catch (Exception e)
            {
                return "Party does not exist";
            }
        }
        return "Incorrect SessionId";
    }

    @Override
    public List<Parties> findExistingParties(int sId) {
        if (sId == sessionId) {
            String query = "select partyName,logoUrl,candidateId,wardNumber,voterId from parties";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Parties.class));
        }
        return null;
    }

    @Override
    public List<Parties> findPartyByName(int sId, String name) {
        if (sId == sessionId) {
            try {
                String query = "select partyName,logoUrl,candidateId,wardNumber,voterId from parties where partyName=partyName";
                return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Parties.class));
            } catch (Exception e) {
                System.out.println("party name not found");

            }
        }
        return null;

    }

    public String updatePartyCandidate(int sId, Parties party) {
        if (sId == sessionId) {
            try {
                String selectQuery = "select candidateId,voterId from candidate where candidateId=" + party.getCandidateId();
                jdbcTemplate.queryForObject(selectQuery, new BeanPropertyRowMapper<>(Candidate.class));
                String query = "update parties set candidateId='" + party.getCandidateId() + "',voterId='" + party.getVoterId() + "' where partyName='" + party.getPartyName() + "'";
                jdbcTemplate.update(query);
            } catch (Exception e) {
                return "invalid id";
            }
        }
        return null;

    }


    @Override
    public List<Voter> findAllVoters(int sId) {
        if (sId == sessionId) {
            String query = "select userId,voterId,name,gender,age,wardNumber,aadharNumber,mobNumber,address,profileUrl";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Voter.class));
        }
        return null;
    }

    @Override
    public List<Voter> findVoterByVoterId(int sId, String voterId) {
        if (sId == sessionId) {
            try {
                String query = "select userId,voterId,name,gender,age,wardNumber,aadharNumber,mobNumber,address,profileUrl where voterId='" + voterId + "'";
                return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Voter.class));
            } catch (Exception e) {
                System.out.println("id does not exist");
            }
        }
        return null;
    }

    @Override
    public List<User> findAllUsers(int sId) {
        if (sId == sessionId) {
            String query = "select * from user";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(User.class));
        }
        return null;
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
    public String winner() {
        try {
            String query = "select electionName,partyName,candidateId,max(voteCount) from electionPartyMapping";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(String.class));
        }catch (Exception e) {
            return null;
        }
    }
}
