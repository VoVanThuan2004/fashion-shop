package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class UserResponse {
    private int userId;
    private String fullName;
    private String email;
    private String image;
    private int loyaltyPoints;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean isActive;
}
