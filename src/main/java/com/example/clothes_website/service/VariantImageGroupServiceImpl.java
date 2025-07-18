package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.cloudinary.UploadResult;
import com.example.clothes_website.dto.request.UploadImageGroupRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.VariantImageGroupResponse;
import com.example.clothes_website.entity.AttributeValue;
import com.example.clothes_website.entity.ImageVariant;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.VariantImageGroup;
import com.example.clothes_website.repository.AttributeValueRepository;
import com.example.clothes_website.repository.ImageVariantRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.VariantImageGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VariantImageGroupServiceImpl implements VariantImageGroupService {
    private final VariantImageGroupRepository variantImageGroupRepository;
    private final ProductRepository productRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageVariantRepository imageVariantRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> addImageGroup(UploadImageGroupRequest uploadImageGroupRequest, List<MultipartFile> images) {
        // Check product
        Optional<Product> product = productRepository.findById(uploadImageGroupRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Product ID: " + uploadImageGroupRequest.getProductId() + " không tồn tại")
                            .build());
        }

        // Check attribute value -- màu sắc
        Optional<AttributeValue> attributeValue = attributeValueRepository.findById(uploadImageGroupRequest.getAttributeValueId());
        if (attributeValue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("AttributeValue ID: " + uploadImageGroupRequest.getAttributeValueId() + " không tồn tại")
                            .build());
        }

        // Check attribute value có phải là type = color hay không
        if (!attributeValue.get().getAttribute().getType().equals("color")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Loại thuộc tính không phải là màu sắc")
                            .build());
        }

        // Check xem productId, attributeValueId nếu tồn tại -> đã có ảnh rồi
        Optional<VariantImageGroup> variantImageGroup = variantImageGroupRepository.findByProductIdAnAndAttributeValueId(
                uploadImageGroupRequest.getProductId(),
                uploadImageGroupRequest.getAttributeValueId()
        );
        if (variantImageGroup.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.CONFLICT.value())
                            .message("Ảnh cho màu sắc này đã tồn tại.")
                            .build());
        }

        // Tạo VariantGroupImage
        VariantImageGroup v1 = new VariantImageGroup();
        v1.setProduct(product.get());
        v1.setAttributeValue(attributeValue.get());
        v1 = variantImageGroupRepository.save(v1);

        // thêm các ảnh cho màu sắc của product
        List<ImageVariant> imageVariants = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                UploadResult uploadResult = cloudinaryService.uploadCloudinaryImage(image);
                ImageVariant imageVariant = new ImageVariant();
                imageVariant.setImagePath(uploadResult.getUrl());
                imageVariant.setPublicId(uploadResult.getPublicId());
                imageVariant.setVariantImageGroup(v1);
                imageVariants.add(imageVariant);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message(e.getMessage())
                                .build());
            }
        }

        imageVariantRepository.saveAll(imageVariants);

        List<String> imagePaths = imageVariants.stream()
                .map(ImageVariant::getImagePath)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Ảnh đã được thêm thành công")
                        .data(Map.of(
                                "groupId", v1.getGroupId(),
                                "productId", v1.getProduct().getProductId(),
                                "attributeValueId", v1.getAttributeValue().getAttributeValueId(),
                                "imagePaths", imagePaths
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateImageGroup(int groupId, List<MultipartFile> images) {
        // Check variantImageGroup có tồn tại
        Optional<VariantImageGroup> variantImageGroup = variantImageGroupRepository.findById(groupId);
        if (variantImageGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("VariantImageGroup ID: " + groupId + " không tồn tại")
                            .build());
        }

        // Xóa các ảnh cũ
        for (ImageVariant imageVariant: variantImageGroup.get().getImageVariants()) {
            if (imageVariant.getPublicId() != null) {
                cloudinaryService.deleteImage(imageVariant.getPublicId());
            }
        }
        imageVariantRepository.deleteAll(variantImageGroup.get().getImageVariants());

        // Update ảnh mới
        List<ImageVariant> imageVariants = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                UploadResult uploadResult = cloudinaryService.uploadCloudinaryImage(image);
                ImageVariant imageVariant = new ImageVariant();
                imageVariant.setImagePath(uploadResult.getUrl());
                imageVariant.setPublicId(uploadResult.getPublicId());
                imageVariant.setVariantImageGroup(variantImageGroup.get());
                imageVariants.add(imageVariant);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message(e.getMessage())
                                .build());
            }
        }

        imageVariantRepository.saveAll(imageVariants);

        List<String> imagePaths = imageVariants.stream()
                .map(ImageVariant::getImagePath)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Ảnh của sản phẩm đã được cập nhật")
                        .data(Map.of(
                                "groupId", groupId,
                                "productId", variantImageGroup.get().getProduct().getProductId(),
                                "attributeValueId", variantImageGroup.get().getAttributeValue().getAttributeValueId(),
                                "imagePaths", imagePaths
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getImagesGroupByProductId(int productId) {
        // Check product
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NO_CONTENT.value())
                            .message("Product ID: " + productId + " không tồn tại")
                            .build());
        }

        List<VariantImageGroup> variantImageGroups = variantImageGroupRepository.findByProductId(productId);

        List<VariantImageGroupResponse> variantImageGroupResponses = variantImageGroups.stream()
                .map(variantImageGroup -> VariantImageGroupResponse.builder()
                        .groupId(variantImageGroup.getGroupId())
                        .productId(variantImageGroup.getProduct().getProductId())
                        .attributeValueId(variantImageGroup.getAttributeValue().getAttributeValueId())
                        .imagesPath(variantImageGroup.getImageVariants().stream()
                                .map(ImageVariant::getImagePath)
                                .toList()
                        )
                        .build())
                .toList();


        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách ảnh theo product")
                        .data(variantImageGroupResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getImagesGroupByGroupId(int groupId) {
        // Check variantImageGroup có tồn tại
        Optional<VariantImageGroup> variantImageGroup = variantImageGroupRepository.findById(groupId);
        if (variantImageGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("VariantImageGroup ID: " + groupId + " không tồn tại")
                            .build());
        }

        VariantImageGroupResponse variantImageGroupResponse = VariantImageGroupResponse.builder()
                .groupId(variantImageGroup.get().getGroupId())
                .productId(variantImageGroup.get().getProduct().getProductId())
                .attributeValueId(variantImageGroup.get().getAttributeValue().getAttributeValueId())
                .imagesPath(variantImageGroup.get().getImageVariants().stream()
                        .map(ImageVariant::getImagePath)
                        .toList()
                )
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy chi tiết ảnh theo groupID: " + groupId)
                        .data(variantImageGroupResponse)
                        .build());
    }
}
