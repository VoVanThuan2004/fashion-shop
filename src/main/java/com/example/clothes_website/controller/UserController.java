package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.*;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/v1/admin/users")
    public ApiResponse<?> getAllCustomers(
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAllCustomers(pageable);
    }

    // Tạo tài khoản
    @PostMapping("/api/v1/register/users")
    public ResponseEntity<ApiResponse<?>> createCustomerAccount(@RequestBody UserCreate userCreate) {
        return userService.createCustomerAccount(userCreate);
    }

    // Verify OTP tạo tài khoản
    @PostMapping("/api/v1/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOTPCreateAccount(@RequestBody VerifyOTP verifyOTP) {
        return userService.verifyOTPCreateAccount(verifyOTP);
    }

    // Khóa tài khoản
    @PutMapping("/api/v1/admin/lock-user/{userId}")
    public ResponseEntity<ApiResponse<?>> banUser(@PathVariable int userId) {
        return userService.banUser(userId);
    }

    // Cập nhật tài khoản
    @PutMapping("/api/v1/update/users/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable int userId,
            @RequestPart UserUpdate userUpdate,
            @RequestPart(required = false) MultipartFile image
    ) {
        return userService.updateUser(userId, userUpdate, image);
    }

    // Thay đổi mật khẩu
    @PutMapping("/api/v1/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return userService.changePassword(changePasswordDTO, userId);
    }

    // Khôi phục mật khẩu
    @PutMapping("/api/v1/recovery-password")
    public ResponseEntity<ApiResponse<?>> recoveryPassword(@RequestParam String email) {
        return userService.recoveryPassword(email);
    }

    // Nhập mã OTP xác nhận khôi phục mật khẩu
    @PutMapping("/api/v1/verify-otp/password")
    public ResponseEntity<ApiResponse<?>> verifyOTPPassword(@RequestBody VerifyOTPRecoveryPassword verifyOTPRecoveryPassword) {
        return userService.verifyOTPPassword(verifyOTPRecoveryPassword);
    }


    // Đăng nhập
    @PostMapping("/api/v1/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userService.login(loginRequest, request);
    }

    // Đăng xuất
    @PostMapping("/api/v1/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestBody Map<String, String> request) {
        return userService.logout(request);
    }

    // Refresh token
    @PostMapping("/api/v1/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody Map<String, String> request) {
        return userService.refreshToken(request);
    }
}
