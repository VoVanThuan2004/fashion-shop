package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ResponseEntity<ApiResponse<?>> addProduct(ProductRequest productRequest, MultipartFile image);

    ResponseEntity<ApiResponse<?>> uploadProduct(int productId, ProductRequest productRequest, MultipartFile image);

    ResponseEntity<ApiResponse<?>> deleteProduct(int productId);

    ApiResponse<?> getAllProducts(Pageable pageable);

    ResponseEntity<ApiResponse<?>> updateProductStatus(int productId);

    ResponseEntity<ApiResponse<?>> filterProducts(int page, int size, List<Integer> categoryIds, List<Integer> brandIds, Double minPrice, Double maxPrice);

    ApiResponse<?> searchProducts(int page, int size, String keyword);

    ApiResponse<?> sortProducts(int page, int size, String filter, String sort);

    ApiResponse<?> getAllProductsBelongToCategoryParent(int categoryId, Pageable pageable);
}
