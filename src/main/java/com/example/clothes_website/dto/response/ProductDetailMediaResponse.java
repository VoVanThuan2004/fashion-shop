package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailMediaResponse {
    private int mediaId;
    private String url;
    private String mediaType;
    private String altText;
    private int position;
    private boolean isFeatured;
}
