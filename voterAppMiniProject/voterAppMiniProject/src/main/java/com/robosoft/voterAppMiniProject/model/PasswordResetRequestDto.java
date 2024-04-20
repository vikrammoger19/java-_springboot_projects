package com.robosoft.voterAppMiniProject.model;

import lombok.Data;

@Data
public class PasswordResetRequestDto {

    private String phoneNumber;//destination
    private String oneTimePassword;
}
