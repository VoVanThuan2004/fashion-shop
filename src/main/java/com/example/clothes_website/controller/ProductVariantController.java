package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.ProductVariantRequest;
import com.example.clothes_website.dto.request.ProductVariantUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/product-variants/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addProductVariant(@RequestBody ProductVariantRequest productVariantRequest) {
        return productVariantService.addProductVariant(productVariantRequest);
    }

    @PutMapping("/product-variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateProductVariant(@PathVariable int variantId, @RequestBody ProductVariantUpdate productVariantUpdate) {
        return productVariantService.updateProductVariant(variantId, productVariantUpdate);
    }

    @DeleteMapping("/product-variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProductVariant(@PathVariable int variantId) {
        return productVariantService.deleteProductVariant(variantId);
    }

    @GetMapping("/product-variants/{productId}")
    public ResponseEntity<ApiResponse<?>> getAllProductVariantByProductId(@PathVariable int productId) {
        return productVariantService.getAllProductVariantByProductId(productId);
    }


    // Quản lý số lượng tồn kho sp biến thể
    @PutMapping("/product-variants/inventory/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateInventory(
            @PathVariable int variantId,
            @RequestBody Map<String, Object> body
    ) {
        return productVariantService.updateInventory(variantId, body);
    }

    // Tắt & bật sản phẩm biến thể
    @PutMapping("/product-variants/status/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateStatusProductVariants(@PathVariable int variantId) {
        return productVariantService.updateStatusProductVariants(variantId);
    }

    // Chọn 1 sản phẩm biến thể theo màu sắc
    @GetMapping("/product-variants/select-attribute/")
    public ResponseEntity<ApiResponse<?>> selectAttribute(
            @RequestParam int productId,
            @RequestParam List<Integer> attributeValueIds) {
        return productVariantService.selectAttribute(productId, attributeValueIds);
    }
}
