package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductVariantImageResponse {
    private int imageId;
    private String url;
    private String publicId;
    private int position;
    private boolean isPrimary;
}
