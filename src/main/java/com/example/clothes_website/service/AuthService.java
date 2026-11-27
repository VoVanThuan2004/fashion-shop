package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ChangePasswordDTO;
import com.example.clothes_website.dto.request.LoginRequest;
import com.example.clothes_website.dto.request.UserCreate;
import com.example.clothes_website.dto.request.VerifyOTP;
import com.example.clothes_website.dto.request.VerifyOTPRecoveryPassword;
import com.example.clothes_website.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResult login(LoginRequest request, HttpServletRequest httpRequest);
    void register(UserCreate request);
    void verifyRegistrationOtp(VerifyOTP request);
    void logout(String rawRefreshToken);
    TokenResponse refreshToken(String rawRefreshToken);
    void recoveryPassword(String email);
    void verifyPasswordOtp(VerifyOTPRecoveryPassword request);
    void changePassword(ChangePasswordDTO request, int userId);
}
