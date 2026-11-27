package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductDetailVariantResponse {
    private int productVariantId;
    private String variantName;
    private String sku;
    private double originalPrice;
    private double sellingPrice;
    private boolean isActive;
    private int inventoryQuantity;
    private boolean inStock;
    private List<ProductDetailVariantOptionValueResponse> optionValues;
    private List<ProductDetailMediaResponse> media;
}
