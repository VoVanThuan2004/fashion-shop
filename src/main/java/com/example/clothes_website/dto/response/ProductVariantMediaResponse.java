package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductVariantMediaResponse {
    private int id;
    private int variantId;
    private ProductMediaResponse media;
    private int position;
    private boolean isFeatured;
}
