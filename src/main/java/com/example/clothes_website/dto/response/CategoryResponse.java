package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class CategoryResponse {
    private int categoryId;
    private String categoryName;
    private Integer parentId;
    private Instant createdAt;
    private Instant updatedAt;
}
