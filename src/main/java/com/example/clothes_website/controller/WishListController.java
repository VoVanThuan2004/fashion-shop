package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.WishListResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wish-lists")
public class WishListController {
    private final WishListService wishListService;

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> addToWishList(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        wishListService.addToWishList(productId, customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Added product in wish list")
                        .build()
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> removeFromWishList(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        wishListService.removeFromWishList(productId, customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Remove product from wish list")
                        .build()
        );
    }

    // Xóa toàn bộ wish list của user
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<?>> clearMyWishList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        wishListService.clearMyWishList(customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Clear all wish lists")
                        .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<WishListResponse>>> getWishLists(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WishListResponse> wishListResponses = wishListService.getWishLists(customUserDetails.getUserId(), page, size);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Page<WishListResponse>>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Get wish lists")
                        .data(wishListResponses)
                        .build()
        );
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkProductInWishList(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Boolean isProductInWishList = wishListService.checkProductInWishList(productId, customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Boolean>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Check product in wish list")
                        .data(isProductInWishList)
                        .build()
        );
    }
}
