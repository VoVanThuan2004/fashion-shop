package com.example.clothes_website.dto.response;

import com.example.clothes_website.entity.ProductVariant;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class OrderItemResponse {
    private int orderItemId;

    private String productVariantName;
    private String productVariantImage;
    private double price;
    private int quantity;
}
