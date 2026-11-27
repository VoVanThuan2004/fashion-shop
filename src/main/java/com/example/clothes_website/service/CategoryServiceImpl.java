package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.CategoryRequest;
import com.example.clothes_website.dto.request.CategoryUpdate;
import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.entity.Category;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.CategoryMapper;
import com.example.clothes_website.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getParentCategories() {
        return categoryMapper.mapToResponseList(
                categoryRepository.findByCategoryParentIsNullAndDeletedFalseOrderByCategoryNameAsc()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getChildren(int parentId) {
        getActiveCategory(parentId);
        return categoryMapper.mapToResponseList(
                categoryRepository.findByCategoryParentCategoryIdAndDeletedFalseOrderByCategoryNameAsc(parentId)
        );
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        String categoryName = request.getCategoryName().trim();
        Category parent = null;

        if (request.getParentId() != null) {
            parent = getActiveCategory(request.getParentId());
            if (parent.getCategoryParent() != null) {
                throw new BadRequestException("Chỉ category cha mới có thể chứa category con");
            }
            if (categoryRepository.existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentCategoryId(
                    categoryName, request.getParentId())) {
                throw new BadRequestException("Tên danh mục đã tồn tại trong danh mục cha");
            }
        } else if (categoryRepository.existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentIsNull(categoryName)) {
            throw new BadRequestException("Tên danh mục đã tồn tại");
        }

        Category category = Category.builder()
                .categoryName(categoryName)
                .categoryParent(parent)
                .deleted(false)
                .build();

        return categoryMapper.mapToResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategory(int categoryId, CategoryUpdate request) {
        Category category = getActiveCategory(categoryId);
        String categoryName = request.getCategoryName().trim();

        boolean duplicate = category.getCategoryParent() == null
                ? categoryRepository.existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentIsNullAndCategoryIdNot(
                categoryName, categoryId)
                : categoryRepository.existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentCategoryIdAndCategoryIdNot(
                categoryName, category.getCategoryParent().getCategoryId(), categoryId);

        if (duplicate) {
            throw new BadRequestException("Tên danh mục đã tồn tại trong cùng cấp");
        }

        category.setCategoryName(categoryName);
        return categoryMapper.mapToResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(int categoryId) {
        Category category = getActiveCategory(categoryId);

        if (category.getCategoryChildren().stream().anyMatch(child -> !child.isDeleted())) {
            throw new BadRequestException("Không thể xoá danh mục đang có danh mục con");
        }
        if (category.getProducts().stream().anyMatch(product -> !Boolean.TRUE.equals(product.getIsDeleted()))) {
            throw new BadRequestException("Không thể xoá danh mục đang được sử dụng bởi sản phẩm");
        }

        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private Category getActiveCategory(int categoryId) {
        return categoryRepository.findByCategoryIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));
    }
}
