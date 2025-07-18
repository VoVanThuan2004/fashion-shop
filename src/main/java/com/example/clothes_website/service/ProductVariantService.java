package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ProductVariantRequest;
import com.example.clothes_website.dto.request.ProductVariantUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductVariantService {
    ResponseEntity<ApiResponse<?>> addProductVariant(ProductVariantRequest productVariantRequest);

    ResponseEntity<ApiResponse<?>> updateProductVariant(int variantId, ProductVariantUpdate productVariantUpdate);

    ResponseEntity<ApiResponse<?>> deleteProductVariant(int variantId);

    ResponseEntity<ApiResponse<?>> updateInventory(int variantId, Map<String, Object> body);

    ResponseEntity<ApiResponse<?>> getAllProductVariantByProductId(int productId);

    ResponseEntity<ApiResponse<?>> updateStatusProductVariants(int variantId);

    ResponseEntity<ApiResponse<?>> selectAttribute(int productId, List<Integer> attributeValueId);
}
