package com.robosoft.voterAppMiniProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User
{

    private int userId;
    private String userName;
    private String mobileNumber;
    private String password;
}
