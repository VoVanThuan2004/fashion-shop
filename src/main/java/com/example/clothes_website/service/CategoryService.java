package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getParentCategories();

    List<CategoryResponse> getChildren(int parentId);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(int categoryId, CategoryUpdate request);

    void deleteCategory(int categoryId);
}
