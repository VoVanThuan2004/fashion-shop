package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ProductOptionRequest;
import com.example.clothes_website.dto.request.ProductOptionValueRequest;
import com.example.clothes_website.dto.response.ProductOptionResponse;
import com.example.clothes_website.dto.response.ProductOptionValueResponse;

import java.util.List;

public interface ProductOptionService {
    List<ProductOptionResponse> getOptions(int productId);

    ProductOptionResponse createOption(int productId, ProductOptionRequest request);

    ProductOptionResponse updateOption(int productId, int optionId, ProductOptionRequest request);

    void deleteOption(int productId, int optionId);

    ProductOptionValueResponse createValue(int productId, int optionId, ProductOptionValueRequest request);

    ProductOptionValueResponse updateValue(int productId, int optionId, int valueId, ProductOptionValueRequest request);

    void deleteValue(int productId, int optionId, int valueId);
}
