package com.example.clothes_website.repository;

import com.example.clothes_website.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WishListRepository extends JpaRepository<WishList, Integer> {
}
