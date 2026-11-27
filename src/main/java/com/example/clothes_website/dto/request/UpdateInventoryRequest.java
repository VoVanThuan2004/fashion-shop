package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInventoryRequest {
    @NotNull(message = "Số lượng không được để trống")
    @PositiveOrZero(message = "Số lượng tồn kho không được âm")
    private Integer quantity;
}
