package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.BrandRequest;
import com.example.clothes_website.dto.response.BrandResponse;
import com.example.clothes_website.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandController {
    private final BrandService brandService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        List<BrandResponse> brandResponses = brandService.getAllBrands();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<List<BrandResponse>>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách thương hiệu")
                        .data(brandResponses)
                .build());
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandResponse>> addBrand(@Valid @RequestBody BrandRequest brandRequest) {
        BrandResponse brandResponse = brandService.addBrand(brandRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BrandResponse>builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Thêm thương hiệu mới thành công")
                        .data(brandResponse)
                        .build()
        );
    }

    @PutMapping("/{brandId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable int brandId,
            @Valid @RequestBody BrandRequest brandRequest) {
        BrandResponse brandResponse = brandService.updateBrand(brandId, brandRequest);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<BrandResponse>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật thương hiệu thành công")
                        .data(brandResponse)
                        .build()
        );
    }

    @DeleteMapping("/{brandId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteBrand(@PathVariable int brandId) {
        brandService.deleteBrand(brandId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Xóa thương hiệu thành công")
                        .build()
        );
    }
}
