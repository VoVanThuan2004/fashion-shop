package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.ChangePasswordDTO;
import com.example.clothes_website.dto.request.LoginRequest;
import com.example.clothes_website.dto.request.UserCreate;
import com.example.clothes_website.dto.request.VerifyOTP;
import com.example.clothes_website.dto.request.VerifyOTPRecoveryPassword;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.LoginResponse;
import com.example.clothes_website.dto.response.TokenResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.AuthService;
import com.example.clothes_website.service.LoginResult;
import com.example.clothes_website.service.RefreshTokenCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenCookieService refreshTokenCookieService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        LoginResult result = authService.login(request, httpRequest);
        refreshTokenCookieService.add(httpResponse, result.refreshToken());
        return ResponseEntity.ok(success("Đăng nhập thành công", result.response()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody UserCreate request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success("Đăng ký thành công", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyRegistrationOtp(@Valid @RequestBody VerifyOTP request) {
        authService.verifyRegistrationOtp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success("Xác thực tài khoản thành công", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse httpResponse
    ) {
        authService.logout(refreshTokenCookieService.get(request));
        refreshTokenCookieService.clear(httpResponse);
        return ResponseEntity.ok(success("Đăng xuất thành công", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(HttpServletRequest request) {
        String rawRefreshToken = refreshTokenCookieService.get(request);
        return ResponseEntity.ok(success("Refresh token thành công", authService.refreshToken(rawRefreshToken)));
    }

    @PutMapping("/recovery-password")
    public ResponseEntity<ApiResponse<Void>> recoveryPassword(@RequestParam String email) {
        authService.recoveryPassword(email);
        return ResponseEntity.ok(success("OTP code sent to your email", null));
    }

    @PutMapping("/verify-otp/password")
    public ResponseEntity<ApiResponse<Void>> verifyPasswordOtp(
            @Valid @RequestBody VerifyOTPRecoveryPassword request,
            HttpServletResponse httpResponse
    ) {
        authService.verifyPasswordOtp(request);
        refreshTokenCookieService.clear(httpResponse);
        return ResponseEntity.ok(success("Khôi phục mật khẩu thành công", null));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordDTO request,
            HttpServletResponse httpResponse
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        authService.changePassword(request, userDetails.getUserId());
        refreshTokenCookieService.clear(httpResponse);
        return ResponseEntity.ok(success("Đổi mật khẩu thành công", null));
    }

    private <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }
}
