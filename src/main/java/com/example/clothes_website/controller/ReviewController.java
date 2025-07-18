package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.ReviewRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/v1/reviews/")
    public ResponseEntity<ApiResponse<?>> addReview(
            @RequestPart ReviewRequest reviewRequest,
            @RequestPart(required = false) List<MultipartFile> images
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getId();

        return reviewService.addReview(reviewRequest, images, userId);
    }

    @GetMapping("/api/v1/reviews/{productId}")
    public ResponseEntity<ApiResponse<?>> getAllReviews(
            @PathVariable int productId,
            @RequestParam("page") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {

        return reviewService.getAllReviews(productId, page, size);
    }
}
