package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter @Getter
@Builder
public class ProductResponse {
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
