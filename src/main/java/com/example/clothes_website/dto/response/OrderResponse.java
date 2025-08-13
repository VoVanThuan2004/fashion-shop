package com.example.clothes_website.dto.response;

import com.example.clothes_website.entity.Coupon;
import com.example.clothes_website.entity.OrderItem;
import com.example.clothes_website.entity.Users;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class OrderResponse {
    private String orderId;
    private String orderCode;

    private String fullName;
    private String shippingAddress;

    private String couponCode;
    private Double discountPrice;

    private Integer loyaltyPoints;
    private Integer totalQuantity;
    private Double totalPrice;

    private String currentStatus;

    private String paymentMethod;
    private LocalDateTime createTime;

    private List<OrderItemResponse> orderItemResponses;
}
