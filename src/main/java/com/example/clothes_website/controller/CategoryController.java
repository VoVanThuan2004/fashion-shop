package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // Lấy danh sách category cha, vd: Áo, Quần,...
    @GetMapping("/api/v1/category-parent")
    public ResponseEntity<ApiResponse<?>> getAllCategoryParent() {
        return categoryService.getAllCategoryParent();
    }

    // Lấy tất cả các category con thuộc về 1 category cha
    @GetMapping("/api/v1/category/{categoryParentId}")
    public ResponseEntity<ApiResponse<?>> getAllCategoryByParentId(@PathVariable int categoryParentId) {
        return categoryService.getAllCategoryByParentId(categoryParentId);
    }

    @PostMapping("/api/v1/category-parent")
    public ResponseEntity<ApiResponse<?>> addParentCategory(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.addParentCategory(categoryRequest);
    }

    @PostMapping("/api/v1/category/{categoryParentId}")
    public ResponseEntity<ApiResponse<?>> addCategoryByParentId(@PathVariable int categoryParentId, @RequestBody CategoryRequest categoryRequest) {
        return categoryService.addCategoryByParentId(categoryParentId, categoryRequest);
    }

    @DeleteMapping("/api/v1/category/{categoryParentId}")
    public ResponseEntity<ApiResponse<?>> deleteCategoryByParentId(@PathVariable int categoryParentId) {
        return categoryService.deleteCategoryByParentId(categoryParentId);
    }

    // Update category parent
    @PutMapping("/api/v1/category-parent/{categoryId}")
    public ResponseEntity<ApiResponse<?>> updateParentCategory(@PathVariable int categoryId, @RequestBody CategoryUpdate categoryUpdate) {
        return categoryService.updateParentCategory(categoryId, categoryUpdate);
    }

}
