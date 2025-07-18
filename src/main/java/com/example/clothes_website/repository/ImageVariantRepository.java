package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ImageVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageVariantRepository extends JpaRepository<ImageVariant, Integer> {

    @Query("select image from ImageVariant image where image.imagePath = :variantId")
    List<ImageVariant> getAllImagesByVariantId(@Param("variantId") int variantId);
}
