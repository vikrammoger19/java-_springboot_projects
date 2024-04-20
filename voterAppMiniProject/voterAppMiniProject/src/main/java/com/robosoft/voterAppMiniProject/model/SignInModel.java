package com.robosoft.voterAppMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignInModel {
    private Long phoneNumber;
    private String password;
}
