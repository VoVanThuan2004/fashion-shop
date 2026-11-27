package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductOptionResponse {
    private int optionId;
    private String name;
    private int position;
    private List<ProductOptionValueResponse> values;
}
