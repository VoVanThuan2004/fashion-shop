package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class UserProfileResponse {
    private int userId;
    private String fullName;
    private String email;
    private String avatar;
    private List<String> roles;
}
