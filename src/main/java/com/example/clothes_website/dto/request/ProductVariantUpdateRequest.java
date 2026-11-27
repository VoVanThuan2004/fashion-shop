package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductVariantUpdateRequest {
    @Size(max = 100, message = "SKU tối đa 100 ký tự")
    private String sku;

    @NotNull(message = "Giá gốc không được để trống")
    @PositiveOrZero(message = "Giá gốc không được âm")
    private Double originalPrice;

    @NotNull(message = "Giá bán không được để trống")
    @PositiveOrZero(message = "Giá bán không được âm")
    private Double sellingPrice;

    private List<Integer> optionValueIds;
}
