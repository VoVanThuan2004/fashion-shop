package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductOptionValueResponse {
    private int optionValueId;
    private String value;
    private String hexCode;
    private int position;
}
