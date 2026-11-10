package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryResponse {
    private int categoryId;
    private String categoryName;
    private Instant createTime;
    private Instant updateTime;
}
