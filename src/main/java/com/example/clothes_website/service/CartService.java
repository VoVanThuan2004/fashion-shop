package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<ApiResponse<?>> getCart(Integer userId, HttpServletRequest request);

    ResponseEntity<ApiResponse<?>> addCart(Integer userId, Integer productVariantId, Integer quantity, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ApiResponse<?>> deleteCart(Integer cartItemId);

    ResponseEntity<ApiResponse<?>> updateCartQuantity(Integer cartItemId, Integer quantity);
}
