package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("select p from Product p where " +
            ":categoryIds is null or p.category.categoryId in :categoryIds and " +
            ":brandIds is null or p.brand.brandId in :brandIds and " +
            ":minPrice is null or p.price >= :minPrice and " +
            ":maxPrice is null or p.price <= :maxPrice"
    )
    Page<Product> filterProduct(
            Pageable pageable,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("brandIds") List<Integer> brandIds,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );


    @Query("select p from Product p where lower(p.name) like lower(concat('%', :keyword, '%'))")
    Page<Product> searchProductsByName(Pageable pageable, @Param("keyword") String keyword);

    // Lọc sản phẩm theo name giam dan - tang dan
    @Query("select p from Product p order by p.name asc")
    Page<Product> sortProductsByNameAsc(Pageable pageable);

    @Query("select p from Product p order by p.name desc")
    Page<Product> sortProductsByNameDesc(Pageable pageable);

    // Lọc sản phẩm theo price giam dan - tang dan
    @Query("select p from Product p order by p.price asc")
    Page<Product> sortProductsByPriceAsc(Pageable pageable);

    @Query("select p from Product p order by p.price desc")
    Page<Product> sortProductsByPriceDesc(Pageable pageable);


    @Query("select p from Product p where p.category.categoryParent.categoryId = :categoryId")
    Page<Product> findAllProductsBelongToCategoryParent(@Param("categoryId") int categoryId, Pageable pageable);
}


