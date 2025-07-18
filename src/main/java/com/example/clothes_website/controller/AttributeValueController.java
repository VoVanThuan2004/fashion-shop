package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.AttributeValueRequest;
import com.example.clothes_website.dto.request.AttributeValueUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.AttributeValueResponse;
import com.example.clothes_website.service.AttributeValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttributeValueController {
    private final AttributeValueService attributeValueService;

    @PostMapping("/api/v1/attribute-value/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addAttributeValue(@RequestBody AttributeValueRequest attributeValueRequest) {
        return attributeValueService.addAttributeValue(attributeValueRequest);
    }

    @PutMapping("/api/v1/attribute-value/{attributeValueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateAttributeValue(@PathVariable int attributeValueId, @RequestBody AttributeValueUpdate attributeValueUpdate) {
        return attributeValueService.updateAttributeValue(attributeValueId, attributeValueUpdate);
    }

    @DeleteMapping("/api/v1/attribute-value/{attributeValueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteAttributeValue(@PathVariable int attributeValueId) {
        return attributeValueService.deleteAttributeValue(attributeValueId);
    }

    @GetMapping("/api/v1/attribute-value/{attributeId}")
    public ResponseEntity<ApiResponse<?>> getAttributeValues(@PathVariable int attributeId) {
        return attributeValueService.getAttributeValues(attributeId);
    }
}
