package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.GenerateVariantsRequest;
import com.example.clothes_website.dto.request.ProductVariantCreateRequest;
import com.example.clothes_website.dto.request.ProductVariantUpdateRequest;
import com.example.clothes_website.dto.request.UpdateInventoryRequest;
import com.example.clothes_website.dto.response.ProductVariantDetailResponse;

import java.util.List;

public interface AdminProductVariantService {
    List<ProductVariantDetailResponse> getVariants(int productId);

    ProductVariantDetailResponse createVariant(int productId, ProductVariantCreateRequest request);

    List<ProductVariantDetailResponse> generateVariants(int productId, GenerateVariantsRequest request);

    ProductVariantDetailResponse updateVariant(int variantId, ProductVariantUpdateRequest request);

    void deleteVariant(int variantId);

    ProductVariantDetailResponse updateInventory(int variantId, UpdateInventoryRequest request);

    ProductVariantDetailResponse updateStatus(int variantId);
}
