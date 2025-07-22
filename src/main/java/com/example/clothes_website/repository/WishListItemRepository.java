package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.WishListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishListItemRepository extends JpaRepository<WishListItem, Integer> {

    @Query("""
        select item.product from WishListItem item
        where item.wishList.wishListId = :wish_list_id
    """)
    Page<Product> getAllProductsBelongToWishList(Pageable pageable, @Param("wish_list_id") Integer wish_list_id);

    @Query("""
        select item from WishListItem item
        where item.wishList.wishListId = :wishListId and item.product.productId = :productId
    """)
    Optional<WishListItem> findByWishListIdAndProductId(
            @Param("wishListId") int wishListId,
            @Param("productId") int productId
    );
}
