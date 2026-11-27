package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.BrandResponse;
import com.example.clothes_website.dto.response.CategoryResponse;
import com.example.clothes_website.dto.response.ProductDetailResponse;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.ProductMedia;
import com.example.clothes_website.entity.ProductOption;
import com.example.clothes_website.entity.ProductVariant;
import com.example.clothes_website.entity.ProductVariantMedia;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.ProductDetailMapper;
import com.example.clothes_website.repository.ProductMediaRepository;
import com.example.clothes_website.repository.ProductOptionRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.ProductVariantMediaRepository;
import com.example.clothes_website.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMediaRepository productMediaRepository;
    private final ProductVariantMediaRepository productVariantMediaRepository;
    private final ProductDetailMapper productDetailMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(int productId) {
        Product product = productRepository.findById(productId)
                .filter(item -> Boolean.TRUE.equals(item.getIsActive())
                        && !Boolean.TRUE.equals(item.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        List<ProductMedia> productMedia = productMediaRepository
                .findByProductProductIdOrderByPositionAscMediaIdAsc(productId);
        List<ProductOption> options = productOptionRepository
                .findByProductProductIdWithValuesOrderByPositionAsc(productId);
        List<ProductVariant> variants = productVariantRepository.findActiveDetailByProductId(productId);

        Map<Integer, List<ProductMedia>> mediaByVariantId = new HashMap<>();
        for (ProductVariantMedia association : productVariantMediaRepository
                .findActiveByProductIdOrderByVariantAndPosition(productId)) {
            mediaByVariantId.computeIfAbsent(
                    association.getProductVariant().getProductVariantId(),
                    ignored -> new ArrayList<>()
            ).add(association.getMedia());
        }

        Integer defaultVariantId = variants.stream()
                .filter(variant -> variant.getInventory() != null
                        && variant.getInventory().getQuantity() > 0)
                .map(ProductVariant::getProductVariantId)
                .findFirst()
                .orElse(variants.isEmpty() ? null : variants.get(0).getProductVariantId());

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imagePath(product.getImagePath())
                .isActive(Boolean.TRUE.equals(product.getIsActive()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .brand(product.getBrand() == null ? null : BrandResponse.builder()
                        .brandId(product.getBrand().getBrandId())
                        .brandName(product.getBrand().getBrandName())
                        .createdAt(product.getBrand().getCreatedAt())
                        .updatedAt(product.getBrand().getUpdatedAt())
                        .build())
                .category(product.getCategory() == null ? null : CategoryResponse.builder()
                        .categoryId(product.getCategory().getCategoryId())
                        .categoryName(product.getCategory().getCategoryName())
                        .parentId(product.getCategory().getCategoryParent() == null
                                ? null : product.getCategory().getCategoryParent().getCategoryId())
                        .createdAt(product.getCategory().getCreatedAt())
                        .updatedAt(product.getCategory().getUpdatedAt())
                        .build())
                .media(productMedia.stream().map(productDetailMapper::mapMedia).toList())
                .options(options.stream().map(productDetailMapper::mapOption).toList())
                .variants(variants.stream()
                        .map(variant -> productDetailMapper.mapVariant(
                                variant,
                                mediaByVariantId.getOrDefault(variant.getProductVariantId(), productMedia)
                        ))
                        .toList())
                .defaultVariantId(defaultVariantId)
                .build();
    }
}
