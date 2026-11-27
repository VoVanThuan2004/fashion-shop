package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ProductVariantOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantOptionValueRepository extends JpaRepository<ProductVariantOptionValue, Integer> {
    boolean existsByOptionValueOptionValueId(int optionValueId);

    boolean existsByOptionValueOptionOptionId(int optionId);
}
