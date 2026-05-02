package com.satyasocial.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String pan;
    private String username;
    private String password;
    private String state;
    private String ageBracket;
    private String gender;
}