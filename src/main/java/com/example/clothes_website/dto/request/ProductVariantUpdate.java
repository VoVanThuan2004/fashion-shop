package com.example.clothes_website.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductVariantUpdate {
    private double originalPrice;
    private double sellingPrice;
    private List<Integer> attributeValueIds;
    private int variantImageGroupId;
}
