package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductVariantDetailResponse {
    private int productVariantId;
    private int productId;
    private String variantName;
    private String sku;
    private String optionSignature;
    private double originalPrice;
    private double sellingPrice;
    private boolean isActive;
    private int inventoryQuantity;
    private List<ProductOptionValueResponse> optionValues;
    private List<ProductVariantImageResponse> images;
}
