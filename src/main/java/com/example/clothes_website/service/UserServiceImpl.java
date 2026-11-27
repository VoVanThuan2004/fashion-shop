package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.dto.request.UserUpdate;
import com.example.clothes_website.dto.response.UserProfileResponse;
import com.example.clothes_website.dto.response.UserResponse;
import com.example.clothes_website.dto.response.UserUpdateResponse;
import com.example.clothes_website.entity.UserRole;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final TokenService tokenService;

    @Override
    public Page<UserResponse> getAllCustomers(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createTime").descending()
        );

        return userRepository.findCustomers(sortedPageable).map(this::toResponse);
    }

    @Override
    public void toggleUserStatus(int userId) {
        Users user = getUser(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public UserUpdateResponse updateUser(int userId, UserUpdate request, MultipartFile image) {
        Users user = getUser(userId);

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new BadRequestException("Full name is required");
        }

        if (image != null && !image.isEmpty()) {
            try {
                user.setImage(cloudinaryService.uploadImage(image));
            } catch (Exception exception) {
                throw new BadRequestException("Failed to upload image");
            }
        }

        user.setFullName(request.getFullName().trim());
        user.setUpdateTime(java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        Users savedUser = userRepository.save(user);

        return UserUpdateResponse.builder()
                .user(toResponse(savedUser))
                .accessToken(tokenService.createAccessToken(savedUser))
                .build();
    }

    @Override
    public UserProfileResponse getUserProfile(int userId) {
        Users currentUser = getUser(userId);

        return UserProfileResponse.builder()
                .userId(currentUser.getUserId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .avatar(currentUser.getImage())
                .roles(currentUser.getUserRoles()
                        .stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .toList()
                )
                .build();
    }

    private Users getUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toResponse(Users user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .image(user.getImage())
                .loyaltyPoints(user.getLoyaltyPoints())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .isActive(user.isActive())
                .build();
    }
}
