package com.example.clothes_website.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductRequest {
    private String name;
    private String description;
    private int categoryId;
    private int brandId;
}
