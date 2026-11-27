package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AssignProductVariantMediaRequest;
import com.example.clothes_website.dto.response.ProductVariantMediaResponse;

import java.util.List;

public interface ProductVariantMediaService {
    List<ProductVariantMediaResponse> getVariantMedia(int variantId);

    List<ProductVariantMediaResponse> assignMedia(
            int variantId,
            AssignProductVariantMediaRequest request
    );

    void removeMedia(int variantId, int mediaId);
}
