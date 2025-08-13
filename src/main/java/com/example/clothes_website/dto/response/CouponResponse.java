package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class CouponResponse {
    private int couponId;
    private String couponCode;
    private double discountPrice;
    private int quantity;
    private boolean isActive;
    private LocalDateTime expiredTime;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
