package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    @Query("""
        select pv.productVariantId from ProductVariant pv
        join VariantAttribute va on va.productVariant = pv
        where pv.product.productId = :productId
        and va.attributeValue.attributeValueId in :attributeValueIds
        group by pv.productVariantId
        having count (distinct va.attributeValue.attributeValueId) = :size
    """)
    List<Integer> findMatchingVariantIds(
            @Param("productId") int productId,
            @Param("attributeValueIds") List<Integer> attributeValueIds,
            @Param("size") int size
    );

    @Query("""
        select pv from ProductVariant pv
        join VariantAttribute va on va.productVariant.productVariantId = pv.productVariantId
        where pv.product.productId = :productId
        and va.attributeValue.attributeValueId in :attributeValueIds
        group by pv
        having count (distinct va.attributeValue.attributeValueId) = :size
    """)
    Optional<ProductVariant> findVariantsByProductIdAndAttributeValue(
            @Param("productId") int productId,
            @Param("attributeValueIds") List<Integer> attributeValueIds,
            @Param("size") int size
    );
}
