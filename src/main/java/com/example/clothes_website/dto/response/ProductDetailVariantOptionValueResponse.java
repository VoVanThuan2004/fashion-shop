package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailVariantOptionValueResponse {
    private int optionId;
    private String optionName;
    private int optionValueId;
    private String value;
    private String hexCode;
}
