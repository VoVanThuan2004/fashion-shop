package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("""
        select cart from Cart cart
        where cart.sessionId = :sessionId
    """)
    Optional<Cart> findCartBySessionId(@Param("sessionId") String sessionId);


    @Query("""
        select cart from Cart cart
        where cart.user.userId = :userId
    """)
    Optional<Cart> findCartByUserId(@Param("userId") Integer userId);
}
