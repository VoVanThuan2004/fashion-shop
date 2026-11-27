package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 200, message = "Tên sản phẩm phải từ 2 đến 200 ký tự")
    private String name;

    @Size(max = 5000, message = "Mô tả không được vượt quá 5000 ký tự")
    private String description;

    @NotNull(message = "Danh mục không được để trống")
    @Positive(message = "ID danh mục phải là số dương")
    private Integer categoryId;

    @NotNull(message = "Thương hiệu không được để trống")
    @Positive(message = "ID thương hiệu phải là số dương")
    private Integer brandId;
}
