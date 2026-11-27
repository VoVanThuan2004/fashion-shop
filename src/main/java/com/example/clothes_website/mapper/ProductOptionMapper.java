package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.ProductOptionResponse;
import com.example.clothes_website.dto.response.ProductOptionValueResponse;
import com.example.clothes_website.entity.ProductOption;
import com.example.clothes_website.entity.ProductOptionValue;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductOptionMapper {

    public ProductOptionResponse mapToResponse(ProductOption option) {
        List<ProductOptionValueResponse> values = option.getValues().stream()
                .sorted(Comparator.comparingInt(ProductOptionValue::getPosition))
                .map(this::mapValue)
                .toList();

        return ProductOptionResponse.builder()
                .optionId(option.getOptionId())
                .name(option.getName())
                .position(option.getPosition())
                .values(values)
                .build();
    }

    public ProductOptionValueResponse mapValue(ProductOptionValue value) {
        return ProductOptionValueResponse.builder()
                .optionValueId(value.getOptionValueId())
                .value(value.getValue())
                .hexCode(value.getHexCode())
                .position(value.getPosition())
                .build();
    }
}
