package com.example.clothes_website.repository;

import com.example.clothes_website.entity.ImageMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMessageRepository extends JpaRepository<ImageMessages, Integer> {
}
