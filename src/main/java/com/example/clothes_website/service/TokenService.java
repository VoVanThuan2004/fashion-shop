package com.example.clothes_website.service;

import com.example.clothes_website.entity.Users;

public interface TokenService {
    String createAccessToken(Users user);

    String createRefreshToken(Users user);
}
