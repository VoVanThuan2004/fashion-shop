package com.example.clothes_website.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponRequest {
    private String couponCode;
    private Double discountPrice;
    private Integer quantity;
    private LocalDateTime expiredTime;
}
