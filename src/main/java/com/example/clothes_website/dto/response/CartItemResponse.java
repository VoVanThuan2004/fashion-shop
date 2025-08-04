package com.example.clothes_website.dto.response;

import lombok.*;

@Getter @Setter
@Builder
public class CartItemResponse {
    private int cartItemId;
    private int productVariantId;
    private String productName;
    private String productImage;
    private double productPrice;
    private int quantity;
}
