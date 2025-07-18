package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BrandRequest {
    private String brandName;
}
