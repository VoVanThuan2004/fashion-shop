package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int refreshTokenId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    private String userAgent;
    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Instant expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
