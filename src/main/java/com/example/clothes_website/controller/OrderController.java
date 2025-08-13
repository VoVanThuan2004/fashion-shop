package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.OrderRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.enums.OrderStatus;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> placeOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = customUserDetails.getUserId();

        return orderService.placeOrder(userId, orderRequest, request);
    }

    // Cập nhật trạng thái đơn hàng
    @PutMapping("/status/")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(@RequestParam String orderCode, @RequestParam OrderStatus orderStatus) {
        return orderService.updateOrderStatus(orderCode, orderStatus);
    }

    // Danh sách orders của 1 người dùng
    @GetMapping("/users/")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> getAllOrdersOfUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();
        return orderService.getAllOrdersOfUser(userId, page, size);
    }

    // Danh sách orders (dashboard admin)
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();
        return orderService.getAllOrders(userId, page, size);
    }
}
