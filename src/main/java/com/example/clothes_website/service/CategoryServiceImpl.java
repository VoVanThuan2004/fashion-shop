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
        category = categoryRepository.save(category);

        // Trả về danh mục mới sau khi thêm
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Thêm danh mục thành công")
                        .data(categoryResponse)
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
                            .message("Danh mục cha không tồn tại")
                            .build());
        }

        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCategoryParent(parentCategory.get());
        category.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        category = categoryRepository.save(category);

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Added category children")
                        .data(categoryResponse)
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
                            .message("Danh mục không tồn tại")
                            .build());
        }

        Category category = parentCategory.get();

        // Kiểm tra nếu là danh mục cha (không có parent)
        if (category.getCategoryParent() == null) {
            // Kiểm tra nếu có danh mục con
            if (!category.getCategoryChildren().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Danh mục cha hiện đang chứa danh mục con")
                                .build());
            }
            // Kiểm tra nếu có sản phẩm
            if (!category.getProducts().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Danh mục hiện đang có sản phẩm tồn tại")
                                .build());
            }
        }
        // Nếu là danh mục con (có parent)
        else {
            // Kiểm tra nếu có sản phẩm
            if (!category.getProducts().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Danh mục hiện đang có sản phẩm tồn tại")
                                .build());
            }
            // Danh mục con không thể có danh mục con khác (theo thiết kế của bạn)
            // Nhưng vẫn kiểm tra để chắc chắn
            if (!category.getCategoryChildren().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Danh mục con không thể chứa danh mục khác")
                                .build());
            }
        }

        categoryRepository.delete(category);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Xóa danh mục thành công")
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
                            .message("Danh mục không tồn tại")
                            .build());
        }

        // Check nếu parentId là khác 0 -> update Category con, ngược lại
        Category categoryUpdated = null;
        if (categoryUpdate.getParentId() != null) {
            Optional<Category> parentCategoryOptional = categoryRepository.findById(categoryUpdate.getParentId());
            if (parentCategoryOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Danh mục cha không tồn tại")
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

//            categoryUpdated = parentCategoryOptional.get();
            category.get().setCategoryName(categoryUpdate.getCategoryName());
            category.get().setCategoryParent(parentCategoryOptional.get());
            category.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            categoryUpdated = categoryRepository.save(category.get());
        }
        // Cập nhật thông tin danh mục cha
        else {
            category.get().setCategoryName(categoryUpdate.getCategoryName());
            category.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            categoryUpdated = categoryRepository.save(category.get());
        }



        CategoryResponse categoryResponse = CategoryResponse.builder()
                .categoryId(categoryUpdated.getCategoryId())
                .categoryName(categoryUpdated.getCategoryName())
                .createTime(categoryUpdated.getCreateTime())
                .updateTime(categoryUpdated.getUpdateTime())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Cập nhật danh mục thành công")
                        .data(categoryResponse)
                        .build());
    }

}
