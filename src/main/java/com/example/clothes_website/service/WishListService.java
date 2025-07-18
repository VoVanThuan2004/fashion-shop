package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface WishListService {
    ResponseEntity<ApiResponse<?>> getWishList(int userId, int page, int size);
}
