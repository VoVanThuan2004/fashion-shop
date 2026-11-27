package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AssignProductVariantMediaRequest;
import com.example.clothes_website.dto.response.ProductVariantMediaResponse;
import com.example.clothes_website.entity.ProductMedia;
import com.example.clothes_website.entity.ProductVariant;
import com.example.clothes_website.entity.ProductVariantMedia;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.ProductMediaMapper;
import com.example.clothes_website.repository.ProductMediaRepository;
import com.example.clothes_website.repository.ProductVariantMediaRepository;
import com.example.clothes_website.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantMediaServiceImpl implements ProductVariantMediaService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductMediaRepository productMediaRepository;
    private final ProductVariantMediaRepository productVariantMediaRepository;
    private final ProductMediaMapper productMediaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantMediaResponse> getVariantMedia(int variantId) {
        getVariant(variantId);
        return productVariantMediaRepository
                .findByProductVariantProductVariantIdOrderByPositionAscIdAsc(variantId)
                .stream()
                .map(productMediaMapper::mapVariantMediaToResponse)
                .toList();
    }

    @Override
    public List<ProductVariantMediaResponse> assignMedia(
            int variantId,
            AssignProductVariantMediaRequest request
    ) {
        ProductVariant variant = getVariant(variantId);
        List<Integer> mediaIds = request.getMediaIds();

        if (mediaIds == null || mediaIds.isEmpty()) {
            throw new BadRequestException("Danh sách media không được để trống");
        }

        if (mediaIds.stream().distinct().count() != mediaIds.size()) {
            throw new BadRequestException("Media bị trùng");
        }
        if (request.getFeaturedMediaId() != null && !mediaIds.contains(request.getFeaturedMediaId())) {
            throw new BadRequestException("Featured media phải nằm trong danh sách media");
        }

        List<ProductMedia> foundMediaList = productMediaRepository.findAllById(mediaIds);
        if (foundMediaList.size() != mediaIds.size()) {
            throw new ResourceNotFoundException("Media không tồn tại");
        }

        Map<Integer, ProductMedia> mediaById = foundMediaList.stream()
                .collect(Collectors.toMap(ProductMedia::getMediaId, Function.identity()));
        List<ProductMedia> mediaList = mediaIds.stream()
                .map(mediaById::get)
                .toList();

        int productId = variant.getProduct().getProductId();
        if (mediaList.stream().anyMatch(media -> media.getProduct() == null
                || media.getProduct().getProductId() != productId)) {
            throw new BadRequestException("Media không thuộc sản phẩm của biến thể");
        }

        productVariantMediaRepository.deleteByProductVariantProductVariantId(variantId);

        List<ProductVariantMedia> associations = new java.util.ArrayList<>(mediaList.size());
        for (int index = 0; index < mediaList.size(); index++) {
            ProductMedia media = mediaList.get(index);
            ProductVariantMedia association = ProductVariantMedia.builder()
                    .productVariant(variant)
                    .media(media)
                    .position(index)
                    .isFeatured(request.getFeaturedMediaId() != null
                            ? media.getMediaId() == request.getFeaturedMediaId()
                            : index == 0)
                    .build();
            associations.add(association);
        }

        productVariantMediaRepository.saveAll(associations);

        return getVariantMedia(variantId);
    }

    @Override
    public void removeMedia(int variantId, int mediaId) {
        getVariant(variantId);
        ProductVariantMedia association = productVariantMediaRepository
                .findByProductVariantProductVariantIdAndMediaMediaId(variantId, mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media chưa được gắn với biến thể"));

        productVariantMediaRepository.delete(association);
    }

    private ProductVariant getVariant(int variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm biến thể không tồn tại"));
    }
}
