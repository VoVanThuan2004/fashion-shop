package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AttributeRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.AttributeResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AttributeService {
    ResponseEntity<ApiResponse<?>> addAttribute(AttributeRequest attributeRequest);

    List<AttributeResponse> getAllAttributes();

    ResponseEntity<ApiResponse<?>> updateAttribute(int attributeId, AttributeRequest attributeRequest);
}
