package com.example.clothes_website.service;

import com.cloudinary.Api;
import com.example.clothes_website.dto.request.AttributeValueRequest;
import com.example.clothes_website.dto.request.AttributeValueUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.AttributeValueResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AttributeValueService {
    ResponseEntity<ApiResponse<?>> addAttributeValue(AttributeValueRequest attributeValueRequest);

    ResponseEntity<ApiResponse<?>> updateAttributeValue(int attributeValueId, AttributeValueUpdate attributeValueUpdate);

    ResponseEntity<ApiResponse<?>> deleteAttributeValue(int attributeValueId);

    ResponseEntity<ApiResponse<?>> getAttributeValues(int attributeId);
}
