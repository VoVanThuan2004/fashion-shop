package com.example.clothes_website.repository;

import com.example.clothes_website.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface WishListRepository extends JpaRepository<WishList, Long> {

    @Query("""
        SELECT w
        FROM WishList w
        WHERE w.product.productId = :productId AND w.user.userId = :userId
    """)
    Optional<WishList> findByProductIdAndUserId(
            @Param("productId") Integer productId,
            @Param("userId") int userId);


    @Query("""
        SELECT w
        FROM WishList w
        WHERE w.user.userId = :userId
    """)
    Page<WishList> findAllByUserId(@Param("userId") int userId, Pageable pageable);
}
