package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductMediaUpdateRequest {
    @Size(max = 500, message = "Alt text tối đa 500 ký tự")
    private String altText;

    @Min(value = 0, message = "Position không được âm")
    private Integer position;

    private Boolean isFeatured;
}
