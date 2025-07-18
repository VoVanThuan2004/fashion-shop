package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
