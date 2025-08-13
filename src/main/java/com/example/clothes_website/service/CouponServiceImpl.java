package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.CouponRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.CouponResponse;
import com.example.clothes_website.entity.Coupon;
import com.example.clothes_website.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> createCoupon(CouponRequest couponRequest) {
        // Kiểm tra request body
        if (couponRequest.getCouponCode() == null || couponRequest.getQuantity() == null
            || couponRequest.getDiscountPrice() == null || couponRequest.getExpiredTime() == null
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Vui lòng nhập đầy đủ thông tin: couponCode, quantity, discountPrice, expiredTime")
                            .build());
        }

        // Kiểm tra số lượng
        if (couponRequest.getQuantity() < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Số lượng phải lớn hơn bằng 1")
                            .build());
        }

        // Kiểm tra expiredTime phải lớn hơn thời gian hiện tại
        if (couponRequest.getExpiredTime().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Thời gian hết hạn không hợp lệ, chậm hơn thời gian hiện tại")
                            .build());
        }

        // Tạo coupon
        Coupon coupon = Coupon.builder()
                .couponCode(couponRequest.getCouponCode())
                .quantity(couponRequest.getQuantity())
                .discountPrice(couponRequest.getDiscountPrice())
                .isActive(true)
                .expiredTime(couponRequest.getExpiredTime())
                .createdTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        coupon = couponRepository.save(coupon);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Mã giảm giá tạo thành công")
                        .data(coupon)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAllCoupons();

        // Chuyển sang response
        List<CouponResponse> couponResponses = coupons.stream()
                .map(coupon -> CouponResponse.builder()
                        .couponId(coupon.getCouponId())
                        .couponCode(coupon.getCouponCode())
                        .quantity(coupon.getQuantity())
                        .discountPrice(coupon.getDiscountPrice())
                        .isActive(coupon.isActive())
                        .expiredTime(coupon.getExpiredTime())
                        .createdTime(coupon.getCreatedTime())
                        .updatedTime(coupon.getUpdatedTime())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<List<CouponResponse>>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách mã giảm giá")
                        .data(couponResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateCoupon(Integer couponId, CouponRequest couponRequest) {
        // Kiểm tra Coupon
        Optional<Coupon> coupon = couponRepository.findById(couponId);
        if (coupon.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Mã giảm giá không tồn tại với ID: " + couponId)
                            .build());
        }

        // Cập nhật thông tin Coupon
        coupon.get().setCouponCode(couponRequest.getCouponCode());
        coupon.get().setQuantity(couponRequest.getQuantity());
        coupon.get().setDiscountPrice(couponRequest.getDiscountPrice());
        coupon.get().setExpiredTime(couponRequest.getExpiredTime());
        coupon.get().setUpdatedTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        Coupon updatedCoupon = couponRepository.save(coupon.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Mã giảm giá đã cập nhật thành công")
                        .data(updatedCoupon)
                        .build());
    }
}
