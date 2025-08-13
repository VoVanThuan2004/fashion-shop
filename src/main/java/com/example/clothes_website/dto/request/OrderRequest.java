package com.example.clothes_website.dto.request;

import com.example.clothes_website.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderRequest {
    private Integer cartId;
    private PaymentMethod paymentMethod;
    private Integer couponId;
    private Integer loyaltyPoints;
}
