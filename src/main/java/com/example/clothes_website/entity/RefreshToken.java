package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int refreshTokenId;

    private String refreshToken;
    private String userAgent;
    private String ipAddress;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
