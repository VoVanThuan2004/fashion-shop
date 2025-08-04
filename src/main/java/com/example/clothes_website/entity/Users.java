package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(name = "idx_users_email", columnList = "email")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String fullName;
    private String password;
    private String email;
    private String image;
    private int loyaltyPoints;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean isActive;
    private String resetOtp;
    private LocalDateTime resetOtpExpired;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses;

    // Sản phẩm yêu thích của người dùng
    @OneToOne(mappedBy = "user")
    private WishList wishlist;

    // Giỏ hàng
    @OneToOne(mappedBy = "user")
    private Cart cart;
}
