package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    private int userId;
    private String fullName;
}
