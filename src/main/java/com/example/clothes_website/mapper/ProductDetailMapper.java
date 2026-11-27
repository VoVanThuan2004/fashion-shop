package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.ProductDetailMediaResponse;
import com.example.clothes_website.dto.response.ProductDetailOptionResponse;
import com.example.clothes_website.dto.response.ProductDetailOptionValueResponse;
import com.example.clothes_website.dto.response.ProductDetailVariantOptionValueResponse;
import com.example.clothes_website.dto.response.ProductDetailVariantResponse;
import com.example.clothes_website.entity.ProductMedia;
import com.example.clothes_website.entity.ProductOption;
import com.example.clothes_website.entity.ProductOptionValue;
import com.example.clothes_website.entity.ProductVariant;
import com.example.clothes_website.entity.ProductVariantOptionValue;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductDetailMapper {
    public ProductDetailMediaResponse mapMedia(ProductMedia media) {
        return ProductDetailMediaResponse.builder()
                .mediaId(media.getMediaId())
                .url(media.getUrl())
                .mediaType(media.getMediaType())
                .altText(media.getAltText())
                .position(media.getPosition())
                .isFeatured(media.isFeatured())
                .build();
    }

    public ProductDetailOptionResponse mapOption(ProductOption option) {
        return ProductDetailOptionResponse.builder()
                .optionId(option.getOptionId())
                .name(option.getName())
                .position(option.getPosition())
                .values(option.getValues().stream()
                        .sorted(Comparator.comparingInt(ProductOptionValue::getPosition)
                                .thenComparingInt(ProductOptionValue::getOptionValueId))
                        .map(this::mapOptionValue)
                        .toList())
                .build();
    }

    private ProductDetailOptionValueResponse mapOptionValue(ProductOptionValue value) {
        return ProductDetailOptionValueResponse.builder()
                .optionValueId(value.getOptionValueId())
                .value(value.getValue())
                .hexCode(value.getHexCode())
                .position(value.getPosition())
                .build();
    }

    public ProductDetailVariantResponse mapVariant(ProductVariant variant, List<ProductMedia> media) {
        int quantity = variant.getInventory() == null ? 0 : variant.getInventory().getQuantity();

        List<ProductDetailVariantOptionValueResponse> optionValues = variant.getVariantOptionValues().stream()
                .sorted(Comparator.comparingInt(item -> item.getOptionValue().getOption().getPosition()))
                .map(this::mapVariantOptionValue)
                .toList();

        return ProductDetailVariantResponse.builder()
                .productVariantId(variant.getProductVariantId())
                .variantName(variant.getVariantName())
                .sku(variant.getSku())
                .originalPrice(variant.getOriginalPrice())
                .sellingPrice(variant.getSellingPrice())
                .isActive(variant.isActive())
                .inventoryQuantity(quantity)
                .inStock(quantity > 0)
                .optionValues(optionValues)
                .media(media.stream().map(this::mapMedia).toList())
                .build();
    }

    private ProductDetailVariantOptionValueResponse mapVariantOptionValue(ProductVariantOptionValue value) {
        ProductOptionValue optionValue = value.getOptionValue();
        ProductOption option = optionValue.getOption();

        return ProductDetailVariantOptionValueResponse.builder()
                .optionId(option.getOptionId())
                .optionName(option.getName())
                .optionValueId(optionValue.getOptionValueId())
                .value(optionValue.getValue())
                .hexCode(optionValue.getHexCode())
                .build();
    }
}
