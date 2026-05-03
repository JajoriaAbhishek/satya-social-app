package com.satyasocial.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private Long id;
    private String username;
    private String fullName;
    private String state;
    private String ageBracket;
    private String gender;
    private Boolean isPanVerified;
    private Long followerCount;
    private Long followingCount;
}