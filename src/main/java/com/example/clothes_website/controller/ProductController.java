package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.ProductRequest;
import com.example.clothes_website.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/api/v1/products")
    public ResponseEntity<ApiResponse<?>> addProduct(
            @RequestPart ProductRequest productRequest,
            @RequestPart MultipartFile image
    ) {
        return productService.addProduct(productRequest, image);
    }

    @PutMapping("/api/v1/products/{productId}")
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable int productId,
            @RequestPart ProductRequest productRequest,
            @RequestPart(required = false) MultipartFile image
    ) {
        return productService.uploadProduct(productId, productRequest, image);
    }

    @DeleteMapping("/api/v1/products/{productId}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable int productId) {
        return productService.deleteProduct(productId);
    }

    @GetMapping("/api/v1/products")
    public ApiResponse<?> getAllProducts(
            @RequestParam int page,
            @RequestParam (defaultValue = "15", required = false) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProducts(pageable);
    }

    // Cập nhật trạng thái product
    @PutMapping("/api/v1/products/status/{productId}")
    public ResponseEntity<ApiResponse<?>> updateProductStatus(@PathVariable int productId) {
        return productService.updateProductStatus(productId);
    }


    // Tìm kiếm sản phẩm
    @GetMapping("/api/v1/products/search")
    public ApiResponse<?> searchProducts(
            @RequestParam int page,
            @RequestParam (defaultValue = "15") int size,
            @RequestParam(required = false) String keyword
    ) {
        return productService.searchProducts(page, size, keyword);
    }

    // Lọc product theo category, brand, minPrice - maxPrice
    @GetMapping("/api/v1/products/filter")
    public ResponseEntity<ApiResponse<?>> filterProducts(
            @RequestParam int page,
            @RequestParam (defaultValue = "15", required = false) int size,
            @RequestParam (required = false) List<Integer> categoryIds,
            @RequestParam (required = false) List<Integer> brandIds,
            @RequestParam (required = false, defaultValue = "0") Double minPrice,
            @RequestParam (required = false, defaultValue = "10000000") Double maxPrice
            ) {
        return productService.filterProducts(page, size, categoryIds, brandIds, minPrice, maxPrice);
    }

    // Lọc tên - giá sản phẩm tăng dần - giảm dần
    @GetMapping("/api/v1/products/sort")
    public ApiResponse<?> sortProducts(
            @RequestParam int page,
            @RequestParam (defaultValue = "15", required = false) int size,
            @RequestParam (defaultValue = "name") String filter,
            @RequestParam (defaultValue = "asc") String sort
    ) {
        return productService.sortProducts(page, size, filter, sort);
    }

    // Get danh sách sản phẩm thuộc category cha (vd: category: nam, nữ)
    @GetMapping("/api/v1/products/category-parent/{categoryId}/")
    public ApiResponse<?> getAllProductsBelongToCategoryParent(
            @PathVariable int categoryId,
            @RequestParam int page,
            @RequestParam (defaultValue = "15", required = false) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProductsBelongToCategoryParent(categoryId, pageable);
    }

}
