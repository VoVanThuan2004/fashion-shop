package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.ProductOptionValueResponse;
import com.example.clothes_website.dto.response.ProductVariantDetailResponse;
import com.example.clothes_website.dto.response.ProductVariantImageResponse;
import com.example.clothes_website.entity.ProductOptionValue;
import com.example.clothes_website.entity.ProductVariant;
import com.example.clothes_website.entity.ProductVariantImage;
import com.example.clothes_website.entity.ProductVariantOptionValue;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductVariantMapper {

    public ProductVariantDetailResponse mapToDetail(ProductVariant variant) {
        List<ProductOptionValueResponse> optionValues = variant.getVariantOptionValues().stream()
                .map(ProductVariantOptionValue::getOptionValue)
                .sorted(Comparator.comparingInt((ProductOptionValue value) -> value.getOption().getPosition()))
                .map(value -> ProductOptionValueResponse.builder()
                        .optionValueId(value.getOptionValueId())
                        .value(value.getValue())
                        .hexCode(value.getHexCode())
                        .position(value.getPosition())
                        .build())
                .toList();

        List<ProductVariantImageResponse> images = variant.getVariantImages().stream()
                .sorted(Comparator.comparingInt(ProductVariantImage::getPosition))
                .map(image -> ProductVariantImageResponse.builder()
                        .imageId(image.getImageId())
                        .url(image.getUrl())
                        .publicId(image.getPublicId())
                        .position(image.getPosition())
                        .isPrimary(image.isPrimary())
                        .build())
                .toList();

        int inventoryQuantity = variant.getInventory() != null ? variant.getInventory().getQuantity() : 0;

        return ProductVariantDetailResponse.builder()
                .productVariantId(variant.getProductVariantId())
                .productId(variant.getProduct() != null ? variant.getProduct().getProductId() : 0)
                .variantName(variant.getVariantName())
                .sku(variant.getSku())
                .optionSignature(variant.getOptionSignature())
                .originalPrice(variant.getOriginalPrice())
                .sellingPrice(variant.getSellingPrice())
                .isActive(variant.isActive())
                .inventoryQuantity(inventoryQuantity)
                .optionValues(optionValues)
                .images(images)
                .build();
    }
}
