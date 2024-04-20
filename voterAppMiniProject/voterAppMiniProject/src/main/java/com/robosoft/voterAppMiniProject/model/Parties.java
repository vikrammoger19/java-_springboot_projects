package com.robosoft.voterAppMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parties {
    private String partyName;
    private MultipartFile logo;
    private String logoUrl;
    private int candidateId;
    private int wardNumber;
    private String voterId;


}
