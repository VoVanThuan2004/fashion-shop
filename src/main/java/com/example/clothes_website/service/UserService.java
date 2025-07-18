package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.*;
import com.example.clothes_website.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    ApiResponse<?> getAllCustomers(Pageable pageable);
    ResponseEntity<ApiResponse<?>> createCustomerAccount(UserCreate userCreate);

    ResponseEntity<ApiResponse<?>> verifyOTPCreateAccount(VerifyOTP verifyOTP);

    ResponseEntity<ApiResponse<?>> banUser(int userId);

    ResponseEntity<ApiResponse<?>> updateUser(int userId, UserUpdate userUpdate, MultipartFile image);

    ResponseEntity<ApiResponse<?>> changePassword(ChangePasswordDTO changePasswordDTO, int userId);

    ResponseEntity<ApiResponse<?>> recoveryPassword(String email);

    ResponseEntity<ApiResponse<?>> verifyOTPPassword(VerifyOTPRecoveryPassword verifyOTPRecoveryPassword);

    ResponseEntity<ApiResponse<?>> login(LoginRequest loginRequest, HttpServletRequest request);

    ResponseEntity<ApiResponse<?>> logout(Map<String, String> request);

    ResponseEntity<ApiResponse<?>> refreshToken(Map<String, String> request);
}
