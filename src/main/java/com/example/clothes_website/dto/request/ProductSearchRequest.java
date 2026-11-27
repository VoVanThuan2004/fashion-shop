package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductSearchRequest {
    @Min(value = 0, message = "Trang phải lớn hơn hoặc bằng 0")
    private int page = 0;

    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    @Max(value = 100, message = "Kích thước trang tối đa là 100")
    private int size = 15;

    @Size(max = 200, message = "Từ khóa tối đa 200 ký tự")
    private String keyword;

    private List<Integer> categoryIds;

    private Integer parentCategoryId;

    private List<Integer> brandIds;

    @PositiveOrZero(message = "Giá tối thiểu không được âm")
    private Double minPrice = 0.0;

    @PositiveOrZero(message = "Giá tối đa không được âm")
    private Double maxPrice;

    private String sortBy = "createdAt";

    private String direction = "desc";
}
