package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ChangePasswordDTO;
import com.example.clothes_website.dto.request.LoginRequest;
import com.example.clothes_website.dto.request.UserCreate;
import com.example.clothes_website.dto.request.VerifyOTP;
import com.example.clothes_website.dto.request.VerifyOTPRecoveryPassword;
import com.example.clothes_website.dto.response.LoginResponse;
import com.example.clothes_website.dto.response.TokenResponse;
import com.example.clothes_website.email.EmailService;
import com.example.clothes_website.entity.Cart;
import com.example.clothes_website.entity.RefreshToken;
import com.example.clothes_website.entity.Role;
import com.example.clothes_website.entity.UserRole;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.exception.AuthenticationException;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.repository.CartRepository;
import com.example.clothes_website.repository.RefreshTokenRepository;
import com.example.clothes_website.repository.RoleRepository;
import com.example.clothes_website.repository.UserRepository;
import com.example.clothes_website.repository.UserRoleRepository;
import com.example.clothes_website.security.TokenPayload;
import com.example.clothes_website.utils.JwtTokenUtil;
import com.example.clothes_website.utils.TokenConstant;
import com.example.clothes_website.utils.TokenHashUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CartRepository cartRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    @Override
    @Transactional
    public LoginResult login(LoginRequest request, HttpServletRequest httpRequest) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Email hoặc mật khẩu không hợp lệ"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Email hoặc mật khẩu không hợp lệ");
        }
        if (!user.isActive()) {
            throw new AuthenticationException("Tài khoản đã bị khóa");
        }

        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = tokenService.createRefreshToken(user);
        RefreshToken tokenEntity = RefreshToken.builder()
                .tokenHash(TokenHashUtil.sha256Hex(refreshToken))
                .user(user)
                .userAgent(httpRequest.getHeader("User-Agent"))
                .ipAddress(httpRequest.getRemoteAddr())
                .expiredAt(Instant.now().plusSeconds(
                        TokenConstant.REFRESH_TOKEN_EXPIRATION
                ))
                .build();
        refreshTokenRepository.save(tokenEntity);

        List<String> roleNames = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .avatar(user.getImage())
                .roles(roleNames)
                .build();

        return new LoginResult(response, refreshToken);
    }

    @Override
    @Transactional
    public void register(UserCreate request) {
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getFullName() == null || request.getFullName().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Please enter all information");
        }

        Users user = userRepository.findByEmail(request.getEmail().trim()).orElseGet(Users::new);
        if (user.getUserId() != 0 && user.isActive()) {
            throw new BadRequestException("Email already exists");
        }

        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        String otp = String.format("%06d", (int) (Math.random() * 1_000_000));

        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);
        user.setResetOtp(passwordEncoder.encode(otp));
        user.setResetOtpExpired(LocalDateTime.now(ZONE).plusMinutes(5));
        Users savedUser = userRepository.save(user);

        if (userRoleRepository.findFirstByUser_UserId(savedUser.getUserId()).isEmpty()) {
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(role);

            userRoleRepository.save(userRole);
        }

        emailService.sendOTPEmailCustomerAccount(savedUser.getEmail(), otp);
    }

    @Override
    @Transactional
    public void verifyRegistrationOtp(VerifyOTP request) {
        Users user = findByEmail(request.getEmail());
        validateOtp(request.getOtp(), user);

        user.setResetOtp(null);
        user.setResetOtpExpired(null);
        user.setActive(true);
        user.setCreateTime(LocalDateTime.now(ZONE));
        Users savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cart.setCartItems(new ArrayList<>());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        if (!StringUtils.hasText(rawRefreshToken)) {
            return;
        }
        refreshTokenRepository.deleteByTokenHash(TokenHashUtil.sha256Hex(rawRefreshToken));
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(String rawRefreshToken) {
        if (!StringUtils.hasText(rawRefreshToken)) {
            throw new AuthenticationException("Refresh token không hợp lệ");
        }

        try {
            if (!JwtTokenUtil.REFRESH_TOKEN.equals(jwtTokenUtil.getTokenType(rawRefreshToken))) {
                throw new AuthenticationException("Refresh token không hợp lệ");
            }

            TokenPayload payload = jwtTokenUtil.getTokenPayload(rawRefreshToken);
            RefreshToken storedToken = refreshTokenRepository
                    .findByTokenHash(TokenHashUtil.sha256Hex(rawRefreshToken))
                    .orElseThrow(() -> new AuthenticationException("Refresh token không tồn tại"));

            if (storedToken.getExpiredAt().isBefore(Instant.now())
                    || storedToken.getUser() == null
                    || storedToken.getUser().getUserId() != payload.getUserId()
                    || !storedToken.getUser().isActive()) {
                throw new AuthenticationException("Refresh token không hợp lệ hoặc đã hết hạn");
            }

            return TokenResponse.builder()
                    .accessToken(tokenService.createAccessToken(storedToken.getUser()))
                    .build();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthenticationException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
    }

    @Override
    @Transactional
    public void recoveryPassword(String email) {
        Users user = findByEmail(email);
        String otp = String.format("%06d", (int) (Math.random() * 1_000_000));
        user.setResetOtp(passwordEncoder.encode(otp));
        user.setResetOtpExpired(LocalDateTime.now(ZONE).plusMinutes(5));
        userRepository.save(user);
        emailService.sendOTPEmailRecoveryPassword(email, otp);
    }

    @Override
    @Transactional
    public void verifyPasswordOtp(VerifyOTPRecoveryPassword request) {
        Users user = findByEmail(request.getEmail());
        validateOtp(request.getOtp(), user);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu không khớp nhau");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpired(null);
        user.setUpdateTime(LocalDateTime.now(ZONE));
        userRepository.save(user);
        refreshTokenRepository.deleteByUserId(user.getUserId());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO request, int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password is incorrect");
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("Old password and new password is the same");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now(ZONE));
        userRepository.save(user);
        refreshTokenRepository.deleteByUserId(user.getUserId());
    }

    private Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email does not exist"));
    }

    private void validateOtp(String otp, Users user) {
        if (user.getResetOtp() == null || !passwordEncoder.matches(otp, user.getResetOtp())) {
            throw new BadRequestException("OTP is incorrect");
        }
        if (user.getResetOtpExpired() == null || user.getResetOtpExpired().isBefore(LocalDateTime.now(ZONE))) {
            throw new BadRequestException("OTP expired");
        }
    }
}
