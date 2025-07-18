package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class ProductVariantResponse {
    private int productId;
    private int productVariantId;
    private String variantName;
    private double sellingPrice;
    private boolean isActive;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<ImageVariantResponse> imageVariants;
    private int inventory;
}
