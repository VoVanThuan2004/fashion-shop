package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.CouponRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.CouponResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CouponService {
    ResponseEntity<ApiResponse<?>> createCoupon(CouponRequest couponRequest);

    ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons();

    ResponseEntity<ApiResponse<?>> updateCoupon(Integer couponId, CouponRequest couponRequest);
}
