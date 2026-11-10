package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.AttributeRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.AttributeResponse;
import com.example.clothes_website.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttributeController {
    private final AttributeService attributeService;

    @PostMapping("/api/v1/attribute/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addAttribute(@RequestBody AttributeRequest attributeRequest) {
        return attributeService.addAttribute(attributeRequest);
    }

    @GetMapping("/api/v1/attribute/")
    public ResponseEntity<ApiResponse<?>> getAllAttributes() {
        return attributeService.getAllAttributes();
    }

    @PutMapping("/api/v1/attribute/{attributeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateAttribute(@PathVariable int attributeId, @RequestBody AttributeRequest attributeRequest) {
        return attributeService.updateAttribute(attributeId, attributeRequest);
    }

    @DeleteMapping("/api/v1/attribute/{attributeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteAttribute(@PathVariable int attributeId) {
        return attributeService.deleteAttribute(attributeId);
    }
}
