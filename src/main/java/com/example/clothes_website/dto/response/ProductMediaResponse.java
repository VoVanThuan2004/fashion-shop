package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductMediaResponse {
    private int mediaId;
    private int productId;
    private String url;
    private String publicId;
    private String mediaType;
    private String altText;
    private int position;
    private boolean isFeatured;
}
