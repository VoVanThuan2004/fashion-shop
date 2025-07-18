package com.example.clothes_website.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class BrandResponse {
    private int brandId;
    private String brandName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
