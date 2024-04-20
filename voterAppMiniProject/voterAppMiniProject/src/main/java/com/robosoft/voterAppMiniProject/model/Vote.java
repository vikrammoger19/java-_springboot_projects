package com.robosoft.voterAppMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    private int wardNumber;
    private String electionName;
    private String partyName;
    private String logoUrl;
    private int candidateId;
}
