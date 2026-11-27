package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.UserUpdate;
import com.example.clothes_website.dto.response.UserProfileResponse;
import com.example.clothes_website.dto.response.UserResponse;
import com.example.clothes_website.dto.response.UserUpdateResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponse> getAllCustomers(Pageable pageable);

    void toggleUserStatus(int userId);

    UserUpdateResponse updateUser(int userId, UserUpdate userUpdate, MultipartFile image);

    UserProfileResponse getUserProfile(int userId);
}
