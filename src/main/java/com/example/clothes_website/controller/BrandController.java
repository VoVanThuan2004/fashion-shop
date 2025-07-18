package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.BrandRequest;
import com.example.clothes_website.dto.response.BrandResponse;
import com.example.clothes_website.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping("/api/v1/brand")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        return brandService.getAllBrands();
    }

    @PostMapping("/api/v1/brand")
    public ResponseEntity<ApiResponse<?>> addBrand(@RequestBody BrandRequest brandRequest) {
        return brandService.addBrand(brandRequest);
    }

    @PutMapping("/api/v1/brand/{brandId}")
    public ResponseEntity<ApiResponse<?>> updateBrand(@PathVariable int brandId, @RequestBody BrandRequest brandRequest) {
        return brandService.updateBrand(brandId, brandRequest);
    }

    @DeleteMapping("/api/v1/brand/{brandId}")
    public ResponseEntity<ApiResponse<?>> deleteBrand(@PathVariable int brandId) {
        return brandService.deleteBrand(brandId);
    }
}
