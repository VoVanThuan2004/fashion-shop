package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.OrderRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

public interface OrderService {
    ResponseEntity<ApiResponse<?>> placeOrder(Integer userId, OrderRequest orderRequest, HttpServletRequest request) throws UnsupportedEncodingException;

    ResponseEntity<ApiResponse<?>> updateOrderStatus(String orderId, OrderStatus orderStatus);

    ResponseEntity<ApiResponse<?>> getAllOrdersOfUser(int userId, int page, int size);

    ResponseEntity<ApiResponse<?>> getAllOrders(int userId, int page, int size);
}
