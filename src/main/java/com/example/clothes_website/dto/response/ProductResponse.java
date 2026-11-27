package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter @Getter
@Builder
public class ProductResponse {
    private int productId;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
