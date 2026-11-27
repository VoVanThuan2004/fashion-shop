package com.example.clothes_website.mapper;

import com.example.clothes_website.dto.response.ProductMediaResponse;
import com.example.clothes_website.dto.response.ProductVariantMediaResponse;
import com.example.clothes_website.entity.ProductMedia;
import com.example.clothes_website.entity.ProductVariantMedia;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMediaMapper {
    public ProductMediaResponse mapToResponse(ProductMedia media) {
        return ProductMediaResponse.builder()
                .mediaId(media.getMediaId())
                .productId(media.getProduct() != null ? media.getProduct().getProductId() : 0)
                .url(media.getUrl())
                .publicId(media.getPublicId())
                .mediaType(media.getMediaType())
                .altText(media.getAltText())
                .position(media.getPosition())
                .isFeatured(media.isFeatured())
                .build();
    }

    public List<ProductMediaResponse> mapToResponseList(List<ProductMedia> media) {
        return media.stream().map(this::mapToResponse).toList();
    }

    public ProductVariantMediaResponse mapVariantMediaToResponse(ProductVariantMedia association) {
        return ProductVariantMediaResponse.builder()
                .id(association.getId())
                .variantId(association.getProductVariant().getProductVariantId())
                .media(mapToResponse(association.getMedia()))
                .position(association.getPosition())
                .isFeatured(association.isFeatured())
                .build();
    }
}
