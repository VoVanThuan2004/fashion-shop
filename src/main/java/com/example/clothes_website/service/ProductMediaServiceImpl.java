package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.cloudinary.UploadResult;
import com.example.clothes_website.dto.request.ProductMediaUpdateRequest;
import com.example.clothes_website.dto.response.ProductMediaResponse;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.ProductMedia;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.ProductMediaMapper;
import com.example.clothes_website.repository.ProductMediaRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.ProductVariantMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductMediaServiceImpl implements ProductMediaService {
    private static final String IMAGE = "IMAGE";
    private static final String VIDEO = "VIDEO";
    private static final String EXTERNAL_VIDEO = "EXTERNAL_VIDEO";
    private static final String MODEL_3D = "MODEL_3D";

    private final ProductRepository productRepository;
    private final ProductMediaRepository productMediaRepository;
    private final ProductVariantMediaRepository productVariantMediaRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMediaMapper productMediaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductMediaResponse> getProductMedia(int productId) {
        getProduct(productId);
        return productMediaMapper.mapToResponseList(
                productMediaRepository.findByProductProductIdOrderByPositionAscMediaIdAsc(productId)
        );
    }

    @Override
    public ProductMediaResponse uploadMedia(
            int productId,
            MultipartFile file,
            String mediaType,
            String altText
    ) {
        Product product = getProduct(productId);
        validateFile(file);
        String normalizedType = normalizeMediaType(mediaType);

        UploadResult uploadResult;
        try {
            uploadResult = cloudinaryService.uploadFile(file);
        } catch (Exception exception) {
            throw new BadRequestException("Upload media thất bại");
        }

        int nextPosition = productMediaRepository
                .findByProductProductIdOrderByPositionAscMediaIdAsc(productId)
                .size();

        ProductMedia media = ProductMedia.builder()
                .url(uploadResult.getUrl())
                .publicId(uploadResult.getPublicId())
                .mediaType(normalizedType)
                .altText(altText)
                .position(nextPosition)
                .isFeatured(productMediaRepository
                        .findByProductProductIdOrderByPositionAscMediaIdAsc(productId)
                        .stream()
                        .noneMatch(ProductMedia::isFeatured))
                .product(product)
                .build();

        return productMediaMapper.mapToResponse(productMediaRepository.save(media));
    }

    @Override
    public ProductMediaResponse updateMedia(int mediaId, ProductMediaUpdateRequest request) {
        ProductMedia media = getMedia(mediaId);
        if (request.getAltText() != null) {
            media.setAltText(request.getAltText());
        }
        if (request.getPosition() != null) {
            media.setPosition(request.getPosition());
        }

        if (Boolean.TRUE.equals(request.getIsFeatured())) {
            clearFeaturedMedia(media.getProduct().getProductId(), mediaId);
            media.setFeatured(true);
        } else if (Boolean.FALSE.equals(request.getIsFeatured())) {
            media.setFeatured(false);
        }

        return productMediaMapper.mapToResponse(productMediaRepository.save(media));
    }

    @Override
    public void deleteMedia(int mediaId) {
        ProductMedia media = getMedia(mediaId);

        if (productVariantMediaRepository.existsByMediaMediaId(mediaId)) {
            throw new BadRequestException("Media đang được sử dụng bởi biến thể");
        }

        if (media.getPublicId() != null && !cloudinaryService.deleteImage(media.getPublicId())) {
            throw new BadRequestException("Không thể xóa media trên Cloudinary");
        }

        productMediaRepository.delete(media);
    }

    private Product getProduct(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
    }

    private ProductMedia getMedia(int mediaId) {
        return productMediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media không tồn tại"));
    }

    private void clearFeaturedMedia(int productId, int exceptMediaId) {
        productMediaRepository.findByProductProductIdOrderByPositionAscMediaIdAsc(productId)
                .stream()
                .filter(media -> media.getMediaId() != exceptMediaId)
                .forEach(media -> media.setFeatured(false));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn media");
        }
    }

    private String normalizeMediaType(String mediaType) {
        String normalized = mediaType == null || mediaType.isBlank()
                ? IMAGE
                : mediaType.trim().toUpperCase();

        if (!List.of(IMAGE, VIDEO, EXTERNAL_VIDEO, MODEL_3D).contains(normalized)) {
            throw new BadRequestException("Loại media không hợp lệ");
        }
        if (!IMAGE.equals(normalized)) {
            throw new BadRequestException("Hiện chỉ hỗ trợ upload media dạng IMAGE");
        }
        return normalized;
    }
}
