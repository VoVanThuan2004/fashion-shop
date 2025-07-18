package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class ProductVariantRequest {
    private int productId;
    private double originalPrice;
    private double sellingPrice;
    private List<Integer> attributeValueIds;
    private int variantImageGroupId;
    private int inventoryQuantity;
}
