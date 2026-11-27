package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.BrandRequest;
import com.example.clothes_website.dto.response.BrandResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrands();

    BrandResponse addBrand(BrandRequest brandRequest);

    BrandResponse updateBrand(int brandId, BrandRequest brandRequest);

    void deleteBrand(int brandId);
}
