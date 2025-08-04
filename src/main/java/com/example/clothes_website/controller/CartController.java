package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add/")
    public ResponseEntity<ApiResponse<?>> addCart(
        @RequestParam Integer productVariantId,
        @RequestParam Integer quantity,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        Integer userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = userDetails.getId();
        }
        return cartService.addCart(userId, productVariantId, quantity, request, response);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getCart(HttpServletRequest request) {
        Integer userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = userDetails.getId();
        }
        return cartService.getCart(userId, request);
    }

    // Xóa sản phẩm ra khỏi giỏ hàng
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<?>> deleteCart(
            @PathVariable Integer cartItemId
    ) {
        return cartService.deleteCart(cartItemId);
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/{cartItemId}/quantity")
    public ResponseEntity<ApiResponse<?>> updateCartQuantity(
            @PathVariable Integer cartItemId,
            @RequestParam Integer quantity
    ) {
        return cartService.updateCartQuantity(cartItemId, quantity);
    }
}
