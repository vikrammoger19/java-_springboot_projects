package com.robosoft.voterAppMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationModel {
    private Long phoneNumber;
    private String password;
    private String secretQuestion;
    private String secretAnswer;
}
