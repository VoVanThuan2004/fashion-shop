package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ProductDetailResponse;

public interface ProductDetailService {
    ProductDetailResponse getProductDetail(int productId);
}
