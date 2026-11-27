package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Integer> {
    List<ProductOptionValue> findByOptionOptionIdOrderByPositionAsc(int optionId);

    boolean existsByOptionOptionIdAndValue(int optionId, String value);

    boolean existsByOptionOptionIdAndValueAndOptionValueIdNot(int optionId, String value, int optionValueId);

    List<ProductOptionValue> findAllByOptionValueIdInAndOptionOptionId(List<Integer> ids, int optionId);
}
