package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("""
        select r from Review r
        where r.product.productId = :productId
    """)
    Page<Review> findByProductId(int productId, Pageable sortedPageable);
}
