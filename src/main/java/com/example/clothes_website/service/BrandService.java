package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.BrandRequest;
import com.example.clothes_website.dto.response.BrandResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BrandService {
    ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands();

    ResponseEntity<ApiResponse<?>> addBrand(BrandRequest brandRequest);

    ResponseEntity<ApiResponse<?>> updateBrand(int brandId, BrandRequest brandRequest);

    ResponseEntity<ApiResponse<?>> deleteBrand(int brandId);
}
