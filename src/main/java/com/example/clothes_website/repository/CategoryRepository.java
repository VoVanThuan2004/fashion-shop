package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("select category from Category category where category.categoryParent is null")
    List<Category> findAllCategoryParent();

    @Query("select category from Category category where category.categoryParent.categoryId = :categoryParentId")
    List<Category> getAllCategoryByParentId(@Param("categoryParentId") int categoryParentId);
}
