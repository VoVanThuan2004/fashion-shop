package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.BrandRequest;
import com.example.clothes_website.dto.response.BrandResponse;
import com.example.clothes_website.entity.Brand;
import com.example.clothes_website.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();

        List<BrandResponse> brandResponses = brands.stream()
                    .map(brand -> BrandResponse.builder()
                            .brandId(brand.getBrandId())
                            .brandName(brand.getBrandName())
                            .createTime(brand.getCreateTime())
                            .updateTime(brand.getUpdateTime())
                            .build())
                    .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<List<BrandResponse>>builder()
                        .status("success")
                        .code(200)
                        .message("Fetch all brands successfully")
                        .data(brandResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addBrand(BrandRequest brandRequest) {
        Brand brand = new Brand();
        brand.setBrandName(brandRequest.getBrandName());
        brand.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        brandRepository.save(brand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Add brand successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateBrand(int brandId, BrandRequest brandRequest) {
        Optional<Brand> brand = brandRepository.findById(brandId);
        if (brand.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(404)
                            .message("Brand not found")
                            .build());
        }

        brand.get().setBrandName(brandRequest.getBrandName());
        brand.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        brandRepository.save(brand.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Update brand successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteBrand(int brandId) {
        Optional<Brand> brand = brandRepository.findById(brandId);
        if (brand.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(404)
                            .message("Brand not found")
                            .build());
        }

        brandRepository.delete(brand.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Delete brand successfully")
                        .build());
    }

}
