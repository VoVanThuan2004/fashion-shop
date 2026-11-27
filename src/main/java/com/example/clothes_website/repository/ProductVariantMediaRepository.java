package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ProductVariantMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantMediaRepository extends JpaRepository<ProductVariantMedia, Integer> {
    List<ProductVariantMedia> findByProductVariantProductVariantIdOrderByPositionAscIdAsc(int variantId);

    @Query("select pvm from ProductVariantMedia pvm " +
            "join fetch pvm.productVariant pv " +
            "join fetch pvm.media media " +
            "where pv.product.productId = :productId and pv.isActive = true " +
            "order by pv.productVariantId asc, pvm.position asc, pvm.id asc")
    List<ProductVariantMedia> findActiveByProductIdOrderByVariantAndPosition(@Param("productId") int productId);

    Optional<ProductVariantMedia> findByProductVariantProductVariantIdAndMediaMediaId(
            int variantId,
            int mediaId
    );

    boolean existsByMediaMediaId(int mediaId);

    void deleteByProductVariantProductVariantId(int variantId);
}
