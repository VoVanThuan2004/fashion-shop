package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ReviewRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ResponseEntity<ApiResponse<?>> addReview(ReviewRequest reviewRequest, List<MultipartFile> images, int userId);
    ResponseEntity<ApiResponse<?>> getAllReviews(int productId,int page, int size);
}
