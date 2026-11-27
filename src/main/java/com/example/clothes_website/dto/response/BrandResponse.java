package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class BrandResponse {
    private int brandId;
    private String brandName;
    private Instant createdAt;
    private Instant updatedAt;
}
