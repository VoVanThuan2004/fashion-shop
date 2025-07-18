package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;

    // Xem danh sách wish list
    @GetMapping("/api/v1/wishlist/")
    public ResponseEntity<ApiResponse<?>> getWishList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getId();

        return wishListService.getWishList(userId, page, size);
    }
}
