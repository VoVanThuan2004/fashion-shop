package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByCategoryParentIsNullAndDeletedFalseOrderByCategoryNameAsc();

    List<Category> findByCategoryParentCategoryIdAndDeletedFalseOrderByCategoryNameAsc(int parentId);

    Optional<Category> findByCategoryIdAndDeletedFalse(int categoryId);

    boolean existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentIsNull(String name);

    boolean existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentCategoryId(
            String name,
            int parentId
    );

    boolean existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentIsNullAndCategoryIdNot(
            String name,
            int categoryId
    );

    boolean existsByCategoryNameIgnoreCaseAndDeletedFalseAndCategoryParentCategoryIdAndCategoryIdNot(
            String name,
            int parentId,
            int categoryId
    );
}
