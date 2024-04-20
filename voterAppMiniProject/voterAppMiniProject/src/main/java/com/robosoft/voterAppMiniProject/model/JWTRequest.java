package com.robosoft.voterAppMiniProject.model;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JWTRequest
{
    private String userName;
    private String password;
}
