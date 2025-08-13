package com.example.clothes_website.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int couponId;

    private String couponCode;
    private Double discountPrice;
    private int quantity;
    private boolean isActive;
    private LocalDateTime expiredTime;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
