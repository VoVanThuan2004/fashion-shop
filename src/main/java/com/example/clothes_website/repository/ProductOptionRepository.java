package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Integer> {
    List<ProductOption> findByProductProductIdOrderByPositionAsc(int productId);

    @Query("select distinct po from ProductOption po left join fetch po.values " +
            "where po.product.productId = :productId order by po.position asc")
    List<ProductOption> findByProductProductIdWithValuesOrderByPositionAsc(@Param("productId") int productId);

    boolean existsByProductProductIdAndName(int productId, String name);

    boolean existsByProductProductIdAndNameAndOptionIdNot(int productId, String name, int optionId);
}
