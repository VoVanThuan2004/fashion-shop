package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AdminProductSearchRequest;
import com.example.clothes_website.dto.request.ProductRequest;
import com.example.clothes_website.dto.request.ProductSearchRequest;
import com.example.clothes_website.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponse addProduct(ProductRequest productRequest, MultipartFile image);

    ProductResponse updateProduct(int productId, ProductRequest productRequest, MultipartFile image);

    void deleteProduct(int productId);

    Page<ProductResponse> getProducts(ProductSearchRequest request);

    Page<ProductResponse> getProductsForAdmin(AdminProductSearchRequest request);

    ProductResponse updateProductStatus(int productId);
}
