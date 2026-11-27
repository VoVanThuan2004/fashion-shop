package com.example.clothes_website.utils;

public class TokenConstant {
    // Access Token
    public static final long ACCESS_TOKEN_EXPIRATION = 60 * 60;      // 1 giờ

    // Refresh Token
    public static final long REFRESH_TOKEN_EXPIRATION = 30L * 24 * 60 * 60;  // 30 ngày

    // Reset password
    public static final long RESET_TOKEN_PASSWORD_EXPIRATION = 5 * 60;
}
