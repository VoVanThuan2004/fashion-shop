package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ProductOptionRequest;
import com.example.clothes_website.dto.request.ProductOptionValueRequest;
import com.example.clothes_website.dto.response.ProductOptionResponse;
import com.example.clothes_website.dto.response.ProductOptionValueResponse;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.ProductOption;
import com.example.clothes_website.entity.ProductOptionValue;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.ProductOptionMapper;
import com.example.clothes_website.repository.ProductOptionRepository;
import com.example.clothes_website.repository.ProductOptionValueRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.ProductVariantOptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductOptionServiceImpl implements ProductOptionService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductVariantOptionValueRepository productVariantOptionValueRepository;
    private final ProductOptionMapper productOptionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionResponse> getOptions(int productId) {
        getProduct(productId);

        return productOptionRepository.findByProductProductIdOrderByPositionAsc(productId).stream()
                .map(productOptionMapper::mapToResponse)
                .toList();
    }

    @Override
    public ProductOptionResponse createOption(int productId, ProductOptionRequest request) {
        Product product = getProduct(productId);

        if (productOptionRepository.existsByProductProductIdAndName(productId, request.getName())) {
            throw new BadRequestException("Tên option đã tồn tại trong sản phẩm");
        }

        ProductOption option = ProductOption.builder()
                .name(request.getName())
                .position(request.getPosition())
                .product(product)
                .build();

        if (request.getValues() != null) {
            List<String> seenValues = new ArrayList<>();
            int index = 0;
            for (ProductOptionValueRequest valueRequest : request.getValues()) {
                String value = valueRequest.getValue().trim();
                if (seenValues.contains(value)) {
                    throw new BadRequestException("Giá trị option bị trùng: " + value);
                }
                seenValues.add(value);

                ProductOptionValue optionValue = ProductOptionValue.builder()
                        .value(value)
                        .hexCode(valueRequest.getHexCode())
                        .position(valueRequest.getPosition() != 0 ? valueRequest.getPosition() : index)
                        .option(option)
                        .build();
                option.getValues().add(optionValue);
                index++;
            }
        }

        productOptionRepository.save(option);

        return productOptionMapper.mapToResponse(option);
    }

    @Override
    public ProductOptionResponse updateOption(int productId, int optionId, ProductOptionRequest request) {
        ProductOption option = getOptionOfProduct(productId, optionId);

        if (!option.getName().equals(request.getName())
                && productOptionRepository.existsByProductProductIdAndNameAndOptionIdNot(productId, request.getName(), optionId)) {
            throw new BadRequestException("Tên option đã tồn tại trong sản phẩm");
        }

        option.setName(request.getName());
        option.setPosition(request.getPosition());
        productOptionRepository.save(option);

        return productOptionMapper.mapToResponse(option);
    }

    @Override
    public void deleteOption(int productId, int optionId) {
        ProductOption option = getOptionOfProduct(productId, optionId);

        if (productVariantOptionValueRepository.existsByOptionValueOptionOptionId(optionId)) {
            throw new BadRequestException("Option đang được sử dụng bởi biến thể, không thể xoá");
        }

        productOptionRepository.delete(option);
    }

    @Override
    public ProductOptionValueResponse createValue(int productId, int optionId, ProductOptionValueRequest request) {
        ProductOption option = getOptionOfProduct(productId, optionId);
        String value = request.getValue().trim();

        if (productOptionValueRepository.existsByOptionOptionIdAndValue(optionId, value)) {
            throw new BadRequestException("Giá trị option đã tồn tại");
        }

        ProductOptionValue optionValue = ProductOptionValue.builder()
                .value(value)
                .hexCode(request.getHexCode())
                .position(request.getPosition())
                .option(option)
                .build();

        productOptionValueRepository.save(optionValue);

        return productOptionMapper.mapValue(optionValue);
    }

    @Override
    public ProductOptionValueResponse updateValue(int productId, int optionId, int valueId, ProductOptionValueRequest request) {
        ProductOptionValue optionValue = getValueOfOption(productId, optionId, valueId);
        String value = request.getValue().trim();

        if (!optionValue.getValue().equals(value)
                && productOptionValueRepository.existsByOptionOptionIdAndValueAndOptionValueIdNot(optionId, value, valueId)) {
            throw new BadRequestException("Giá trị option đã tồn tại");
        }

        optionValue.setValue(value);
        optionValue.setHexCode(request.getHexCode());
        optionValue.setPosition(request.getPosition());
        productOptionValueRepository.save(optionValue);

        return productOptionMapper.mapValue(optionValue);
    }

    @Override
    public void deleteValue(int productId, int optionId, int valueId) {
        ProductOptionValue optionValue = getValueOfOption(productId, optionId, valueId);

        if (productVariantOptionValueRepository.existsByOptionValueOptionValueId(valueId)) {
            throw new BadRequestException("Giá trị option đang được sử dụng bởi biến thể, không thể xoá");
        }

        productOptionValueRepository.delete(optionValue);
    }

    private Product getProduct(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
    }

    private ProductOption getOptionOfProduct(int productId, int optionId) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option không tồn tại"));

        if (option.getProduct() == null || option.getProduct().getProductId() != productId) {
            throw new ResourceNotFoundException("Option không thuộc sản phẩm");
        }

        return option;
    }

    private ProductOptionValue getValueOfOption(int productId, int optionId, int valueId) {
        ProductOptionValue optionValue = productOptionValueRepository.findById(valueId)
                .orElseThrow(() -> new ResourceNotFoundException("Giá trị option không tồn tại"));

        if (optionValue.getOption() == null
                || optionValue.getOption().getOptionId() != optionId
                || optionValue.getOption().getProduct() == null
                || optionValue.getOption().getProduct().getProductId() != productId) {
            throw new ResourceNotFoundException("Giá trị option không thuộc option/sản phẩm");
        }

        return optionValue;
    }
}
