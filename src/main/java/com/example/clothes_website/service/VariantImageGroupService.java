package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.UploadImageGroupRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VariantImageGroupService {
    ResponseEntity<ApiResponse<?>> addImageGroup(UploadImageGroupRequest uploadImageGroupRequest, List<MultipartFile> images);

    ResponseEntity<ApiResponse<?>> updateImageGroup(int groupId, List<MultipartFile> images);

    ResponseEntity<ApiResponse<?>> getImagesGroupByProductId(int productId);

    ResponseEntity<ApiResponse<?>> getImagesGroupByGroupId(int groupId);
}
