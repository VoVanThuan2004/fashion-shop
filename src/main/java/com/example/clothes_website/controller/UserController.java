package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.UserUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.UserProfileResponse;
import com.example.clothes_website.dto.response.UserResponse;
import com.example.clothes_website.dto.response.UserUpdateResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/v1/admin/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllCustomers(
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<UserResponse>>builder()
                .status("success")
                .code(200)
                .message("Get all customers successfully")
                .data(userService.getAllCustomers(pageable))
                .build());
    }

    @PutMapping("/api/v1/admin/lock-user/{userId}")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable int userId) {
        userService.toggleUserStatus(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .code(200)
                .message("Account status updated")
                .build());
    }

    @PutMapping("/api/v1/update/users/{userId}")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUser(
            @PathVariable int userId,
            @RequestPart UserUpdate userUpdate,
            @RequestPart(required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.<UserUpdateResponse>builder()
                .status("success")
                .code(200)
                .message("Account updated successfully")
                .data(userService.updateUser(userId, userUpdate, image))
                .build());
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserProfileResponse userProfileResponse = userService.getUserProfile(customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<UserProfileResponse>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Get user profile successfully")
                        .data(userProfileResponse)
                .build());
    }
}
