package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.entity.Category;
import com.example.clothes_website.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    public ResponseEntity<ApiResponse<?>> getAllCategoryParent() {
        List<Category> categories = categoryRepository.findAllCategoryParent();

        List<CategoryResponse> categoryResponses = categories.stream()
                .map(category -> CategoryResponse.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .createTime(category.getCreateTime())
                        .updateTime(category.getUpdateTime())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Fetch all parent categories")
                        .data(categoryResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllCategoryByParentId(int categoryParentId) {
        // Kiểm tra category cha có tồn tại không
        Optional<Category> parentCategory = categoryRepository.findById(categoryParentId);
        if (parentCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Category parent does not exist")
                            .build());
        }

        // Lấy danh sách category con
        List<Category> categories = categoryRepository.getAllCategoryByParentId(categoryParentId);

        List<CategoryResponse> categoryResponses = categories.stream()
                .map(category -> CategoryResponse.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .createTime(category.getCreateTime())
                        .updateTime(category.getUpdateTime())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Fetch all categories belong to category parent")
                        .data(categoryResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addParentCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        category.setCategoryParent(null);
        categoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Added category parent")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addCategoryByParentId(int categoryParentId, CategoryRequest categoryRequest) {
        Optional<Category> parentCategory = categoryRepository.findById(categoryParentId);
        if (parentCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Parent category does not exist")
                            .build());
        }

        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCategoryParent(parentCategory.get());
        category.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        categoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Added category children")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteCategoryByParentId(int categoryParentId) {
        Optional<Category> parentCategory = categoryRepository.findById(categoryParentId);
        if (parentCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Category parent does not exist")
                            .build());
        }

        categoryRepository.delete(parentCategory.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Deleted category successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateParentCategory(int categoryId, CategoryUpdate categoryUpdate) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Category does not exist")
                            .build());
        }

        // Check nếu parentId là khác 0 -> update Category con, ngược lại
        Category parentCategory = null;
        if (categoryUpdate.getParentId() != 0) {
            Optional<Category> parentCategoryOptional = categoryRepository.findById(categoryUpdate.getParentId());
            if (parentCategoryOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Category Parent does not exist")
                                .build());
            }
            if (categoryId == parentCategoryOptional.get().getCategoryId()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Error Category children must be different with Category parent")
                                .build());
            }

            parentCategory = parentCategoryOptional.get();
        }

        category.get().setCategoryName(categoryUpdate.getCategoryName());
        category.get().setCategoryParent(parentCategory);
        category.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        categoryRepository.save(category.get());



        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Updated category successfully")
                        .build());
    }

}
