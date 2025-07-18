package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.UploadImageGroupRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.service.VariantImageGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VariantImageGroupController {
    private final VariantImageGroupService variantImageGroupService;

    @PostMapping("/api/v1/image-groups/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addImageGroup(
            @RequestPart UploadImageGroupRequest uploadImageGroupRequest,
            @RequestPart("images")List<MultipartFile> images
    ) {
        return variantImageGroupService.addImageGroup(uploadImageGroupRequest, images);
    }

    @PutMapping(value = "/api/v1/image-groups/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateImageGroup(
            @PathVariable int groupId,
            @RequestPart("images") List<MultipartFile> images
    ) {
        return variantImageGroupService.updateImageGroup(groupId, images);
    }

    // Lấy toàn bộ ảnh theo product
    @GetMapping("/api/v1/image-groups/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getImagesGroupByProductId(@PathVariable int productId) {
        return variantImageGroupService.getImagesGroupByProductId(productId);
    }

    // Lấy chi tiết ảnh theo 1 groupId
    @GetMapping("/api/v1/image-groups/{groupId}")
    public ResponseEntity<ApiResponse<?>> getImagesGroupByGroupId(@PathVariable int groupId) {
        return variantImageGroupService.getImagesGroupByGroupId(groupId);
    }
}
