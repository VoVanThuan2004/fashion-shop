package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.LoginResponse;

public record LoginResult(LoginResponse response, String refreshToken) {
}
