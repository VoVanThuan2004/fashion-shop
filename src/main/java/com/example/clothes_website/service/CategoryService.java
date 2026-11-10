package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.dto.response.CategoryResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategoryParent();

    List<CategoryResponse> getAllCategoryByParentId(int categoryParentId);

    CategoryResponse addParentCategory(CategoryRequest categoryRequest);

    CategoryResponse addCategoryByParentId(int categoryParentId, CategoryRequest categoryRequest);

    void deleteCategoryByParentId(int categoryParentId);

    CategoryResponse updateCategory(int categoryId, CategoryUpdate categoryUpdate);
}
