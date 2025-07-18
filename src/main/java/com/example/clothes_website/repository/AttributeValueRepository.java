package com.example.clothes_website.repository;

import com.example.clothes_website.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Integer> {

}
