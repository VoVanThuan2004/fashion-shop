package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ProductMediaUpdateRequest;
import com.example.clothes_website.dto.response.ProductMediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductMediaService {
    List<ProductMediaResponse> getProductMedia(int productId);

    ProductMediaResponse uploadMedia(int productId, MultipartFile file, String mediaType, String altText);

    ProductMediaResponse updateMedia(int mediaId, ProductMediaUpdateRequest request);

    void deleteMedia(int mediaId);
}
