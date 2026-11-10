package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // Lấy danh sách category cha, vd: Áo, Quần,...
    @GetMapping("/api/v1/category-parent")
    public ResponseEntity<ApiResponse<?>> getAllCategoryParent() {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategoryParent();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<List<CategoryResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách danh mục cha thành công")
                .data(categoryResponses)
                .build());
    }

    // Lấy tất cả các category con thuộc về 1 category cha
    @GetMapping("/api/v1/category/{categoryParentId}")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategoryByParentId(@PathVariable int categoryParentId) {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategoryByParentId(categoryParentId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<List<CategoryResponse>>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách danh mục thuộc danh mục cha")
                        .data(categoryResponses)
                .build());
    }

    @PostMapping("/api/v1/category-parent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addParentCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.addParentCategory(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CategoryResponse>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Tạo danh mục cha thành công")
                .data(categoryResponse)
                .build());
    }

    @PostMapping("/api/v1/category/{categoryParentId}")
    public ResponseEntity<ApiResponse<?>> addCategoryByParentId(@PathVariable int categoryParentId, @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.addCategoryByParentId(categoryParentId, categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CategoryResponse>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Tạo danh mục con thành công")
                .data(categoryResponse)
                .build());
    }

    @DeleteMapping("/api/v1/category/{categoryParentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteCategoryByParentId(@PathVariable int categoryParentId) {
        categoryService.deleteCategoryByParentId(categoryParentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.NO_CONTENT.value())
                        .message("Xóa danh mục thành công")
                .build());
    }

    // Update category parent
    @PutMapping("/api/v1/category/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateParentCategory(@PathVariable int categoryId, @RequestBody CategoryUpdate categoryUpdate) {
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, categoryUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<CategoryResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật danh mục thành công")
                .data(categoryResponse)
                .build());
    }

}
