package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {
    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentId(category.getCategoryParent() != null
                        ? category.getCategoryParent().getCategoryId()
                        : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public List<CategoryResponse> mapToResponseList(List<Category> categories) {
        return categories.stream().map(this::mapToResponse).toList();
    }
}
