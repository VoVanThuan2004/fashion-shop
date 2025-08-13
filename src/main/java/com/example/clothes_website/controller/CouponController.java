package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.CouponRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.CouponResponse;
import com.example.clothes_website.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/coupon/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createCoupon(@RequestBody CouponRequest couponRequest) {
        return couponService.createCoupon(couponRequest);
    }

    @GetMapping("/coupon/")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @PutMapping("/coupon/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateCoupon(
            @PathVariable Integer couponId,
            @RequestBody CouponRequest couponRequest
    ) {
        return couponService.updateCoupon(couponId, couponRequest);
    }
}
