package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Builder
public class CategoryUpdate {
    private String categoryName;
    private Integer parentId;
}
