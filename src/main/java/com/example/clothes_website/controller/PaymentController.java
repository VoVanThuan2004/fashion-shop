package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vnpay/payment")
    public ResponseEntity<ApiResponse<?>> createUrlPayment(@RequestParam String orderCode, @RequestParam Long amount, HttpServletRequest request) throws UnsupportedEncodingException {
        return paymentService.createUrlPayment(orderCode, amount, request);
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<ApiResponse<?>> returnPayment(HttpServletRequest request) {
        return paymentService.returnPayment(request);
    }
}
