package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.BrandResponse;
import com.example.clothes_website.entity.Brand;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrandMapper {

    public BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .brandId(brand.getBrandId())
                .brandName(brand.getBrandName())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    public List<BrandResponse> mapToResponseList(List<Brand> brands) {
        return brands.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
