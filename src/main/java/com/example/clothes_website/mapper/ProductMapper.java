package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .imagePath(product.getImagePath())
                .isActive(Boolean.TRUE.equals(product.getIsActive()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
