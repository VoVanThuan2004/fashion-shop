package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateVariantsRequest {
    @NotNull(message = "Giá gốc không được để trống")
    @PositiveOrZero(message = "Giá gốc không được âm")
    private Double originalPrice;

    @NotNull(message = "Giá bán không được để trống")
    @PositiveOrZero(message = "Giá bán không được âm")
    private Double sellingPrice;

    @PositiveOrZero(message = "Số lượng tồn kho không được âm")
    private int inventoryQuantity;
}
