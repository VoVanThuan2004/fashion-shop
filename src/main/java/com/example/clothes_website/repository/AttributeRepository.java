package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    Optional<Attribute> findByCode(String code);
}
