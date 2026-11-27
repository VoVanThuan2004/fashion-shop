package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getParentCategories() {
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách danh mục cha thành công")
                .data(categoryService.getParentCategories())
                .build());
    }

    @GetMapping("/{categoryId}/children")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getChildren(
            @PathVariable int categoryId
    ) {
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách danh mục con thành công")
                .data(categoryService.getChildren(categoryId))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CategoryResponse>builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo danh mục thành công")
                        .data(categoryService.createCategory(request))
                        .build());
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable int categoryId,
            @Valid @RequestBody CategoryUpdate request
    ) {
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật danh mục thành công")
                .data(categoryService.updateCategory(categoryId, request))
                .build());
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable int categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Xóa danh mục thành công")
                .build());
    }
}
