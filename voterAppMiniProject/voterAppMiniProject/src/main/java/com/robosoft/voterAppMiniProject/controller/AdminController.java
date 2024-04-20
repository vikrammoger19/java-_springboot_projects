package com.robosoft.voterAppMiniProject.controller;



import com.robosoft.voterAppMiniProject.model.*;
import com.robosoft.voterAppMiniProject.service.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/Admin")
public class AdminController {
    @Autowired
    AdminServiceImpl adminService;

    @PostMapping("/adminSignUp")

    public ResponseEntity<String> adminSignUp(@RequestBody Admin admin) {
        try {
            return new ResponseEntity<>(adminService.adminRegister(admin), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("already registered", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/adminSignIn/{mobNumber}/{password}")
    public ResponseEntity<Integer> adminSignIn(@PathVariable Long mobNumber, @PathVariable String password) {

        return new ResponseEntity<>(adminService.adminSignIn(mobNumber, password), HttpStatus.OK);
    }

    @PostMapping("/addWard/{sId}")
    public ResponseEntity<String> addWard(@PathVariable int sId, @RequestBody Ward ward) {
        try {
            return new ResponseEntity<>(adminService.addWard(sId, ward), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("ward already exist", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/viewAllWards/{sId}")
    public ResponseEntity<List<Ward>> getAllWards(@PathVariable int sId) {
        if(sId==0) {
            return new ResponseEntity<>(adminService.findAllWards(sId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/viewWardByNumber/{sId}/{wardNumber}")
    public ResponseEntity<List<Ward>> getWardByNumber(@PathVariable int sId, @PathVariable int wardNumber) {
        try {
            return new ResponseEntity<>(adminService.findWardByNumber(sId, wardNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/removeWard/{sId}/{wardNumber}")
    public ResponseEntity<String> deleteWard(@PathVariable int sId, @PathVariable int wardNumber) {
            return new ResponseEntity<>(adminService.deleteWard(sId, wardNumber), HttpStatus.NO_CONTENT);
    }
    @PostMapping("/addElections/{sId}")
    public ResponseEntity<String> addElections(@PathVariable int sId,@RequestBody Election election) {
        return new ResponseEntity<>(adminService.addElections(sId, election), HttpStatus.CREATED);
    }
    @GetMapping("/viewAllElections/{sId}")
    public ResponseEntity<List<Election>> getAllElections(@PathVariable int sId) {
        try {
            return new ResponseEntity<>(adminService.findAllElections(sId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/viewElectionByName/{sId}/{name}")
    public ResponseEntity<List<Election>> getElectionByName(@PathVariable int sId, @PathVariable String name) {
        try {
            return new ResponseEntity<>(adminService.findElectionByName(sId, name), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
    @PutMapping("/updateElectionSchedule/{sId}")
    public ResponseEntity<String> updateElectionSchedule(@PathVariable int sId,@RequestBody Election election) {
        return new ResponseEntity<>(adminService.updateElectionSchedule(sId,election), HttpStatus.CREATED);
    }
    @GetMapping("/Home/{sId}")
    public ResponseEntity<List<Election>> findUpcomingElections(@PathVariable int sId) {
        try {
            return new ResponseEntity<>(adminService.findUpcomingElections(sId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/registerCandidate/{sId}")
    public ResponseEntity<String> addCandidate(@PathVariable int sId, @RequestBody Candidate candidate){
        try {
            return new ResponseEntity<>(adminService.addCandidate(sId,candidate), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("already registered", HttpStatus.NOT_FOUND);
        }

    }
    @GetMapping("/viewAllCandidate/{sId}")
    public ResponseEntity<List<Candidate>> getAllCandidates(@PathVariable int sId){
        try {
            return new ResponseEntity<>(adminService.findAllCandidates(sId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/viewCandidateById/{sId}/{cId}")
    public ResponseEntity<List<Candidate>> getCandidateById(@PathVariable int sId,@PathVariable int cId){
        try {
            return new ResponseEntity<>(adminService.findCandidateById(sId,cId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/removeCandidate/{sId}/{cId}")
    public ResponseEntity<String> deleteCandidate(@PathVariable int sId,@PathVariable int cId){
        if(adminService.deleteCandidate(sId, cId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Successfully");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Task Failed");
    }

    @PostMapping("/registerParties/{sId}")
    public ResponseEntity<String> addParties(@PathVariable int sId,@ModelAttribute Parties parties){
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addParties(sId,parties));
    }
    @GetMapping("/logoUrl/{partyName}/{sId}")
    public ResponseEntity<String> getLogoUrl(@PathVariable String partyName, @PathVariable int sId)
    {
        return new ResponseEntity<>(adminService.getLogoUrl(partyName,sId), HttpStatus.OK);
    }
    @GetMapping("/logo/{partyName}")
    public ResponseEntity<Resource> getPartyLogo(@PathVariable String partyName)
    {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png")).header("Content-Disposition","filename=\"" + partyName + ".png" +"\"").body(new ByteArrayResource(adminService.getPartyLogo(partyName)));
    }
    @GetMapping("/viewExistingParties/{sId}")
    public ResponseEntity<List<Parties>> getExistingParties(@PathVariable int sId){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findExistingParties(sId));
    }
    @GetMapping("/viewPartyByName/{sId}/{name}")
    public ResponseEntity<List<Parties>> getPartyByName(@PathVariable int sId,@PathVariable String name){
        if(name.equalsIgnoreCase(null)) {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.findPartyByName(sId, name));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @PatchMapping("/updatePartyCandidate/{sId}")
    public ResponseEntity<String> updatePartyCandidate(@PathVariable int sId,@RequestBody Parties party){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updatePartyCandidate(sId,party));

    }

    @GetMapping("/viewAllVoters/{sId}")
    public ResponseEntity<List<Voter>> getAllVoters(@PathVariable int sId){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllVoters(sId));
    }
    @GetMapping("/viewVoterByVoterId/{sId}/{voterId}")
    public ResponseEntity<List<Voter>> getVoterByVoterId(@PathVariable int sId,@PathVariable String voterId){
        if(voterId.equalsIgnoreCase(null)) {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.findVoterByVoterId(sId, voterId));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/viewAllUsers/{sId}")
    public ResponseEntity<List<User>> getAllUsers(@PathVariable int sId){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllUsers(sId));
    }

    @GetMapping("/viewElectionResult")
    public ResponseEntity<Election> getElectionResult(){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.electionResult());
    }
    @GetMapping("/viewWinner")
    public ResponseEntity<String> getWinner(){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.winner());
    }
}
