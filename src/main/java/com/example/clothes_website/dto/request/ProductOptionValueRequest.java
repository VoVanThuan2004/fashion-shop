package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOptionValueRequest {
    @NotBlank(message = "Giá trị option không được để trống")
    @Size(max = 100, message = "Giá trị option tối đa 100 ký tự")
    private String value;

    @Pattern(regexp = "^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$", message = "Mã màu không hợp lệ")
    private String hexCode;

    private int position;
}
