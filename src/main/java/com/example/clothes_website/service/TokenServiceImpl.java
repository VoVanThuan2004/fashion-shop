package com.example.clothes_website.service;

import com.example.clothes_website.entity.Users;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.repository.UserRoleRepository;
import com.example.clothes_website.security.TokenPayload;
import com.example.clothes_website.utils.JwtTokenUtil;
import com.example.clothes_website.utils.TokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public String createAccessToken(Users user) {
        return jwtTokenUtil.generateAccessToken(createPayload(user), TokenConstant.ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    public String createRefreshToken(Users user) {
        return jwtTokenUtil.generateRefreshToken(createPayload(user), TokenConstant.REFRESH_TOKEN_EXPIRATION);
    }

    private TokenPayload createPayload(Users user) {
        String role = userRoleRepository.findFirstByUser_UserId(user.getUserId())
                .map(userRole -> userRole.getRole().getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));

        return TokenPayload.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .role(role)
                .build();
    }
}
