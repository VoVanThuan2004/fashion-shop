package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.dto.request.*;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.LoginResponse;
import com.example.clothes_website.dto.response.UserResponse;
import com.example.clothes_website.email.EmailService;
import com.example.clothes_website.entity.*;
import com.example.clothes_website.repository.*;
import com.example.clothes_website.security.JwtTokenUtil;
import com.example.clothes_website.security.TokenPayload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WishListRepository wishListRepository;
    private final CartRepository cartRepository;

    @Override
    public ApiResponse<?> getAllCustomers(Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
          pageable.getPageNumber(),
          pageable.getPageSize(),
                Sort.by("createTime").descending()
        );

        // Lấy danh sách customers
        Page<Users> users = userRepository.findCustomers(sortedPageable);


        // Chuyển sang response
        Page<UserResponse> userResponses = users
                .map(user -> UserResponse.builder()
                        .userId(user.getUserId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .image(user.getImage())
                        .loyaltyPoints(user.getLoyaltyPoints())
                        .createTime(user.getCreateTime())
                        .updateTime(user.getUpdateTime())
                        .isActive(user.isActive())
                        .build());

        return ApiResponse.builder()
                .status("success")
                .code(200)
                .message("Get all customers successfully")
                .data(userResponses)
                .build();
    }

    @Override
    public ResponseEntity<ApiResponse<?>> createCustomerAccount(UserCreate userCreate) {
        if (userCreate.getEmail().isEmpty() || userCreate.getFullName().isEmpty() || userCreate.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Please enter to all information")
                            .data(null)
                            .build());
        }

        // Check email tồn tại
        Optional<Users> emailUser = userRepository.findByEmail(userCreate.getEmail());
        if (emailUser.isPresent() && emailUser.get().isActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Email already exists")
                            .data(null)
                            .build());
        }

        // Tạo tài khoản với isActive = False (chua kich hoat)
        Optional<Role> role = roleRepository.findByRoleName("USER");
        if (role.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Role User doest not create")
                            .data(null)
                            .build());
        }

        // Sinh ma OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));

        Users user = new Users();
        user.setFullName(userCreate.getFullName());
        user.setEmail(userCreate.getEmail());
        user.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        user.setActive(false);
        user.setResetOtp(otp);
        user.setResetOtpExpired(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(5));
        user = userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setRole(role.get());
        userRole.setUser(user);

        userRoleRepository.save(userRole);

        emailService.sendOTPEmailCustomerAccount(userCreate.getEmail(), otp);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Account created successfully. Please check your email to verify your account.")
                        .build());
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse<?>> verifyOTPCreateAccount(VerifyOTP verifyOTP) {
        Optional<Users> users = userRepository.findByEmail(verifyOTP.getEmail());
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Email does not exist")
                            .data(null)
                            .build());
        }

        // Check mã OTP + thời gian hết hạn
        if (!verifyOTP.getOtp().equals(users.get().getResetOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("OTP is incorrect")
                            .data(null)
                            .build());
        }

        if (users.get().getResetOtpExpired().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("OTP expired")
                            .data(null)
                            .build());
        }

        // Tạo tài khoản
        users.get().setResetOtp(null);
        users.get().setResetOtpExpired(null);
        users.get().setActive(true);
        users.get().setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        Optional<Users> userCreated = Optional.of(userRepository.save(users.get()));

        WishList wishList = new WishList();
        wishList.setUser(userCreated.get());
        wishListRepository.save(wishList);

        // Tạo giỏ hàng trống
        Cart cart = new Cart();
        cart.setUser(userCreated.get());
        cart.setCartItems(new ArrayList<>());
        cartRepository.save(cart);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Account created successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> banUser(int userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        // Thay đổi active
        if (user.get().isActive()) {
            user.get().setActive(false);
            userRepository.save(user.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Account user locked")
                            .build());
        }
        else {
            user.get().setActive(true);
            userRepository.save(user.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Account user unlocked")
                            .build());
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateUser(int userId, UserUpdate userUpdate, MultipartFile image) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        if (userUpdate.getFullName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Full name is required")
                            .build());
        }

        // Check image
        String imageUrl = "";
        if (image != null) {
            try {
                imageUrl = cloudinaryService.uploadImage(image);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Failed to upload image")
                                .build());
            }
        }

        // Update user
        user.get().setFullName(userUpdate.getFullName());
        user.get().setImage(Objects.equals(imageUrl, "") ? user.get().getImage() : imageUrl);
        user.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        user = Optional.of(userRepository.save(user.get()));



        Optional<UserRole> userRole = userRoleRepository.findFirstByUser_UserId(userId);
        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User role not found")
                            .build());
        }

        // Tạo lại access token mới theo user đã cập nhật
        final int accessTokenTime = 60 * 60;
        TokenPayload tokenPayload = TokenPayload.builder()
                .userId(userId)
                .fullName(user.get().getFullName())
                .role(userRole.get().getRole().getRoleName())
                .build();

        String accessToken = jwtTokenUtil.generateToken(tokenPayload, accessTokenTime);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Account updated successfully")
                        .data(Map.of(
                                "accessToken", accessToken
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> changePassword(ChangePasswordDTO changePasswordDTO, int userId) {
        // Check user id
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .build());
        }

        // Check password
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Old password is incorrect")
                            .build());
        }

        // Check mật khẩu mới và confirm lại không khớp
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("New password is incorrect")
                            .build());
        }

        // Check mật khẩu mới trùng với mật khẩu cũ
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Old password and new password is the same")
                            .build());
        }

        // Cập nhật mật khẩu mới
        user.get().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        userRepository.save(user.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Account changed password successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> recoveryPassword(String email) {
        // Check email
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Email does not exist")
                            .build());
        }

        // Tạo mã OTP + gửi mail
        String otp = String.format("%06d", new Random().nextInt(1000000));

        user.get().setResetOtp(otp);
        user.get().setResetOtpExpired(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(5));
        userRepository.save(user.get());

        emailService.sendOTPEmailRecoveryPassword(email, otp);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("OTP code sent to your email")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> verifyOTPPassword(VerifyOTPRecoveryPassword verifyOTPRecoveryPassword) {
        // Check email
        Optional<Users> user = userRepository.findByEmail(verifyOTPRecoveryPassword.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Email does not exist")
                            .build());
        }

        // Check OTP
        if (!user.get().getResetOtp().equals(verifyOTPRecoveryPassword.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("OTP code is incorrect")
                            .build());
        }

        // Check expired time OTP
        if (user.get().getResetOtpExpired().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("OTP code is expired")
                            .build());
        }


        // Cập nhật user
        user.get().setPassword(passwordEncoder.encode(verifyOTPRecoveryPassword.getPassword()));
        user.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        user.get().setResetOtp(null);
        user.get().setResetOtpExpired(null);
        userRepository.save(user.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Recovery password successfully")
                        .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> login(LoginRequest loginRequest, HttpServletRequest request) {
        // Check email
        Optional<Users> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Email does not exist")
                            .build());
        }

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Wrong password")
                            .build());
        }

        // Check account locked
        if (!user.get().isActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Account user is locked")
                            .build());
        }

        // Generate time for accessToken
        final int accessTokenTime = 60 * 60;    // 1 hour
        final int refreshTokenTime = 30 * 24 * 60 * 60;     // 30 days


        Optional<UserRole> userRole = userRoleRepository.findFirstByUser_UserId(user.get().getUserId());
        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User role not found")
                            .build());
        }

        // Create TokenPayload
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setUserId(user.get().getUserId());
        tokenPayload.setFullName(user.get().getFullName());
        tokenPayload.setRole(userRole.get().getRole().getRoleName());

        // Generate token
        String accessToken = jwtTokenUtil.generateToken(tokenPayload, accessTokenTime);
        String refreshToken = jwtTokenUtil.generateToken(tokenPayload, refreshTokenTime);

        // Xóa mã RefreshToken cũ nếu tồn tại
        refreshTokenRepository.deleteByUserId(user.get().getUserId());

        // Save RefreshToken
        RefreshToken refreshTokenDB = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user.get())
                .userAgent(request.getHeader("User-Agent"))
                .ipAddress(request.getRemoteAddr())
                .createTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .expireTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusDays(30))
                .build();

        refreshTokenRepository.save(refreshTokenDB);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Successfully logged in")
                        .data(LoginResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .userId(user.get().getUserId())
                                .fullName(user.get().getFullName())
                                .build())
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> logout(Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        Optional<RefreshToken> refreshTokenDB = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenDB.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("RefreshToken not found")
                            .build());
        }

        // Delete refresh token
        refreshTokenRepository.delete(refreshTokenDB.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Successfully logged out")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> refreshToken(Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        Optional<RefreshToken> refreshTokenDB = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenDB.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Refresh Token not found")
                            .build());
        }

        if (refreshTokenDB.get().getExpireTime().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(401)
                            .message("Invalid or expired refresh token")
                            .build());
        }


        Optional<UserRole> userRole = userRoleRepository.findFirstByUser_UserId(refreshTokenDB.get().getUser().getUserId());
        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User role not found")
                            .build());
        }

        // Generate access token
        final int accessTokenTime = 60 * 60;

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setUserId(refreshTokenDB.get().getUser().getUserId());
        tokenPayload.setFullName(refreshTokenDB.get().getUser().getFullName());
        tokenPayload.setRole(userRole.get().getRole().getRoleName());

        String accessToken = jwtTokenUtil.generateToken(tokenPayload, accessTokenTime);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Refresh token success")
                        .data(Map.of(
                                "accessToken", accessToken
                        ))
                        .build());
    }
}
