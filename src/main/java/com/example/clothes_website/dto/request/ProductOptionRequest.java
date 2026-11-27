package com.example.clothes_website.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductOptionRequest {
    @NotBlank(message = "Tên option không được để trống")
    @Size(max = 100, message = "Tên option tối đa 100 ký tự")
    private String name;

    private int position;

    @Valid
    private List<ProductOptionValueRequest> values;
}
