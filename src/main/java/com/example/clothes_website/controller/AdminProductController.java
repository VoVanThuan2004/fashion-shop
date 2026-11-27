package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.AdminProductSearchRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @Valid @ModelAttribute AdminProductSearchRequest request
    ) {
        Page<ProductResponse> productResponses = productService.getProductsForAdmin(request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<ProductResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách sản phẩm cho admin")
                .data(productResponses)
                .build());
    }
}
