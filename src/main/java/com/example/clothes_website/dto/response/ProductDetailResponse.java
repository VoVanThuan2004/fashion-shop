package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ProductDetailResponse {
    private int productId;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private BrandResponse brand;
    private CategoryResponse category;
    private List<ProductDetailMediaResponse> media;
    private List<ProductDetailOptionResponse> options;
    private List<ProductDetailVariantResponse> variants;
    private Integer defaultVariantId;
}
