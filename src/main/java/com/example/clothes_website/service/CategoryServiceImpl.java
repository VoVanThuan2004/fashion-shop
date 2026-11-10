package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.entity.Category;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
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
    public List<CategoryResponse> getAllCategoryParent() {
        List<Category> categories = categoryRepository.findAllCategoryParent();

        return categories.stream()
                .map(category -> CategoryResponse.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .createTime(category.getCreateTime())
                        .updateTime(category.getUpdateTime())
                        .build())
                .toList();
    }

    @Override
    public List<CategoryResponse> getAllCategoryByParentId(int categoryParentId) {
        // Kiểm tra category cha có tồn tại không
        Optional<Category> parentCategory = categoryRepository.findById(categoryParentId);
        if (parentCategory.isEmpty()) {
            throw new BadRequestException("Category parent does not exist");
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

        return categoryResponses;
    }

    @Override
    public CategoryResponse addParentCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCategoryParent(null);
        category = categoryRepository.save(category);

        // Trả về danh mục mới sau khi thêm
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }

    @Override
    public CategoryResponse addCategoryByParentId(int categoryParentId, CategoryRequest categoryRequest) {
        Optional<Category> parentCategory = categoryRepository.findById(categoryParentId);
        if (parentCategory.isEmpty()) {
            throw new ResourceNotFoundException("Danh mục cha không tồn tại");
        }

        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCategoryParent(parentCategory.get());
        category = categoryRepository.save(category);

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }

    @Override
    public void deleteCategoryByParentId(int categoryParentId) {
        Optional<Category> category = categoryRepository.findById(categoryParentId);
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Danh mục không tồn tại");
        }

        category.get().setDeleted(true);
        categoryRepository.save(category.get());
    }

    @Override
    public CategoryResponse updateCategory(int categoryId, CategoryUpdate categoryUpdate) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Danh mục không tồn tại");
        }

        category.get().setCategoryName(categoryUpdate.getCategoryName());
        categoryRepository.save(category.get());


        return CategoryResponse.builder()
                .categoryId(category.get().getCategoryId())
                .categoryName(category.get().getCategoryName())
                .createTime(category.get().getCreateTime())
                .updateTime(category.get().getUpdateTime())
                .build();
    }

}
