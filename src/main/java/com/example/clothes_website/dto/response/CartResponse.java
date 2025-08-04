package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class CartResponse {
    private int cartId;
    private List<CartItemResponse> cartItems;
    private double totalPrice;
    private int totalQuantity;
}
