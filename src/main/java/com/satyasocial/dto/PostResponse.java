package com.satyasocial.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {

    private Long id;
    private String content;
    private String username;
    private String fullName;
    private String state;
    private String ageBracket;
    private String gender;
    private Boolean isPanVerified;
    private Integer likeCount;
    private Integer repostCount;
    private LocalDateTime createdAt;
}