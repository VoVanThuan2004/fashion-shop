package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductMediaRepository extends JpaRepository<ProductMedia, Integer> {
    List<ProductMedia> findByProductProductIdOrderByPositionAscMediaIdAsc(int productId);

    Optional<ProductMedia> findByMediaIdAndProductProductId(int mediaId, int productId);

    boolean existsByProductProductIdAndPublicId(int productId, String publicId);
}
