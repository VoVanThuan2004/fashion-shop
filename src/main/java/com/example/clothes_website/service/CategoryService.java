package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<ApiResponse<?>> getAllCategoryParent();

    ResponseEntity<ApiResponse<?>> getAllCategoryByParentId(int categoryParentId);

    ResponseEntity<ApiResponse<?>> addParentCategory(CategoryRequest categoryRequest);

    ResponseEntity<ApiResponse<?>> addCategoryByParentId(int categoryParentId, CategoryRequest categoryRequest);

    ResponseEntity<ApiResponse<?>> deleteCategoryByParentId(int categoryParentId);

    ResponseEntity<ApiResponse<?>> updateParentCategory(int categoryId, CategoryUpdate categoryUpdate);
}
