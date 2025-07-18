package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Query("select i from Inventory i where i.productVariant.productVariantId = :variantId")
    Optional<Inventory> findByProductVariant(@Param("variantId") int variantId);
}
