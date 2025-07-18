package com.example.clothes_website.repository;

import com.example.clothes_website.entity.VariantImageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VariantImageGroupRepository extends JpaRepository<VariantImageGroup, Integer> {

    @Query("select v from VariantImageGroup v where v.product.productId = :productId and v.attributeValue.attributeValueId = :attributeValueId")
    Optional<VariantImageGroup> findByProductIdAnAndAttributeValueId(@Param("productId") int productId, @Param("attributeValueId") int attributeValueId);

    @Query("select v from VariantImageGroup v where v.product.productId = :productId")
    List<VariantImageGroup> findByProductId(@Param("productId") int productId);
}
