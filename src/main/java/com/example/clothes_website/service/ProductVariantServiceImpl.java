package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.ProductVariantRequest;
import com.example.clothes_website.dto.request.ProductVariantUpdate;
import com.example.clothes_website.dto.response.*;
import com.example.clothes_website.entity.*;
import com.example.clothes_website.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final VariantImageGroupRepository variantImageGroupRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final InventoryRepository inventoryRepository;
    private final VariantAttributeRepository variantAttributeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> addProductVariant(ProductVariantRequest productVariantRequest) {
        // Check product có tồn tại
        Optional<Product> product = productRepository.findById(productVariantRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Product không tồn tại")
                            .build());
        }

        // Check Variant image group (ảnh cho sp biến thể) có tồn tại
        Optional<VariantImageGroup> variantImageGroup = variantImageGroupRepository.findById(productVariantRequest.getVariantImageGroupId());
        if (variantImageGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("ID ảnh của sản phẩm không hợp lệ - groupID: " + productVariantRequest.getVariantImageGroupId())
                            .build());
        }

        // Check các giá trị thuộc tính
        if (productVariantRequest.getAttributeValueIds().size() != 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Attribute Value ID chỉ có 2 giá trị")
                            .build());
        }

        Optional<AttributeValue> v1 = attributeValueRepository.findById(productVariantRequest.getAttributeValueIds().get(0));
        Optional<AttributeValue> v2 = attributeValueRepository.findById(productVariantRequest.getAttributeValueIds().get(1));
        if (v1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Giá trị thuộc tính không tồn tại - ID: " + productVariantRequest.getProductId())
                            .build());
        }
        if (v2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Giá trị thuộc tính không tồn tại - ID: " + productVariantRequest.getProductId())
                            .build());
        }

        // Check biến thể này có tồn tại hay chưa
        List<Integer> matchedIds = productVariantRepository
                .findMatchingVariantIds(productVariantRequest.getProductId(), productVariantRequest.getAttributeValueIds(), productVariantRequest.getAttributeValueIds().size());

        if (!matchedIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(409)
                            .message("Biến thể với các thuộc tính này đã tồn tại")
                            .build());
        }

        // Check giá gốc phải nhỏ hơn giá bán
        if (productVariantRequest.getOriginalPrice() >= productVariantRequest.getSellingPrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Giá gốc phải nhỏ hơn giá bán")
                            .build());
        }

        String colorName = v1.get().getAttribute().getType().equals("color") ? v1.get().getValue() : v2.get().getValue();
        String sizeName = v1.get().getAttribute().getType().equals("size") ? v1.get().getValue() : v2.get().getValue();
        String variantName = product.get().getName() + " " + colorName + " - " + sizeName;

        ProductVariant productVariant = new ProductVariant();
        productVariant.setVariantName(variantName);
        productVariant.setVariantImageGroup(variantImageGroup.get());
        productVariant.setProduct(product.get());
        productVariant.setOriginalPrice(productVariantRequest.getOriginalPrice());
        productVariant.setSellingPrice(productVariantRequest.getSellingPrice());
        productVariant.setActive(true);
        productVariant.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        productVariant = productVariantRepository.save(productVariant);

        // Lưu số lượng tồn kho
        Inventory inventory = new Inventory();
        inventory.setProductVariant(productVariant);
        inventory.setQuantity(productVariantRequest.getInventoryQuantity());
        inventory.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        inventoryRepository.save(inventory);

        // Lưu VariantAttribute
        VariantAttribute variantAttribute1 = new VariantAttribute();
        variantAttribute1.setProductVariant(productVariant);
        variantAttribute1.setAttributeValue(v1.get());

        VariantAttribute variantAttribute2 = new VariantAttribute();
        variantAttribute2.setProductVariant(productVariant);
        variantAttribute2.setAttributeValue(v2.get());

        variantAttributeRepository.save(variantAttribute1);
        variantAttributeRepository.save(variantAttribute2);


        // Cập nhật giá thấp nhất để hiển thị trên product gốc
        double minSellingPrice = product.get().getProductVariants().get(0).getSellingPrice();
        for (int i = 1; i < product.get().getProductVariants().size(); i++) {
            if (product.get().getProductVariants().get(i).getSellingPrice() < minSellingPrice) {
                minSellingPrice = product.get().getProductVariants().get(i).getSellingPrice();
            }
        }

        product.get().setPrice(minSellingPrice);
        productRepository.save(product.get());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Sản phẩm biến thể tạo thành công")
                        .data(Map.of(
                                "productVariantId", productVariant.getProductVariantId(),
                                "variantName", productVariant.getVariantName(),
                                "inventoryQuantity", inventory.getQuantity()
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateProductVariant(int variantId, ProductVariantUpdate productVariantUpdate) {
        // Check Product Variant có tồn tại
        Optional<ProductVariant> productVariant = productVariantRepository.findById(variantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Sản phẩm biến thể không tồn tại - ID: " + variantId)
                            .build());
        }

        // Check Variant image group (ảnh cho sp biến thể) có tồn tại
        Optional<VariantImageGroup> variantImageGroup = variantImageGroupRepository.findById(productVariantUpdate.getVariantImageGroupId());
        if (variantImageGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("ID ảnh của sản phẩm không hợp lệ - groupID: " + productVariantUpdate.getVariantImageGroupId())
                            .build());
        }

        // Check các giá trị thuộc tính
        if (productVariantUpdate.getAttributeValueIds().size() != 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Attribute Value ID chỉ có 2 giá trị")
                            .build());
        }

        Optional<AttributeValue> v1 = attributeValueRepository.findById(productVariantUpdate.getAttributeValueIds().get(0));
        Optional<AttributeValue> v2 = attributeValueRepository.findById(productVariantUpdate.getAttributeValueIds().get(1));
        if (v1.isEmpty() || v2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Giá trị thuộc tính không tồn tại - ID: " + productVariantUpdate.getAttributeValueIds().get(0) + " - " + productVariantUpdate.getAttributeValueIds().get(1))
                            .build());
        }

        // Check giá gốc phải nhỏ hơn giá bán
        if (productVariantUpdate.getOriginalPrice() >= productVariantUpdate.getSellingPrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Giá gốc phải nhỏ hơn giá bán")
                            .build());
        }

        String colorName = v1.get().getAttribute().getType().equals("color") ? v1.get().getValue() : v2.get().getValue();
        String sizeName = v1.get().getAttribute().getType().equals("size") ? v1.get().getValue() : v2.get().getValue();
        String variantName = productVariant.get().getProduct().getName() + " " + colorName + " - " + sizeName;

        productVariant.get().setVariantImageGroup(variantImageGroup.get());
        productVariant.get().setVariantName(variantName);
        productVariant.get().setOriginalPrice(productVariantUpdate.getOriginalPrice());
        productVariant.get().setSellingPrice(productVariantUpdate.getSellingPrice());
        productVariant.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        productVariantRepository.save(productVariant.get());

        // Cập nhật lại bảng VariantAttribute
        variantAttributeRepository.deleteAll(productVariant.get().getVariantAttributes());

        VariantAttribute va1 = new VariantAttribute();
        va1.setProductVariant(productVariant.get());
        va1.setAttributeValue(v1.get());

        VariantAttribute va2 = new VariantAttribute();
        va2.setProductVariant(productVariant.get());
        va2.setAttributeValue(v2.get());

        variantAttributeRepository.saveAll(List.of(va1, va2));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Sản phẩm biến thể đã cập nhật thành công")
                        .data(Map.of(
                                "productVariantId", productVariant.get().getProductVariantId(),
                                "variantName", productVariant.get().getVariantName(),
                                "inventoryQuantity", productVariant.get().getInventory().getQuantity()
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteProductVariant(int variantId) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(variantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Sản phẩm biến thể không tồn tại - ID: " + variantId)
                            .build());
        }

        // Xóa inventory
        Inventory inventory = productVariant.get().getInventory();
        inventoryRepository.delete(inventory);

        // Xóa VariantAttribute
        variantAttributeRepository.deleteAll(productVariant.get().getVariantAttributes());

        // Xóa ProductVariants
        productVariantRepository.delete(productVariant.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Sản phẩm biến thể đẫ xóa thành công")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateInventory(int variantId, Map<String, Object> body) {
        Optional<ProductVariant> productVariant = productVariantRepository.findById(variantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Sản phẩm biến thể không tồn tại - ID: " + variantId)
                            .build());
        }

        // Check quantity
        if (!body.containsKey("quantity")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Thiếu trường thông tin quantity")
                            .build());
        }
        int quantity = (int) body.get("quantity");
        if (quantity < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Số lượng tồn kho phải lớn hơn hoặc bằng 2")
                            .build());
        }

        Inventory inventory = productVariant.get().getInventory();
        inventory.setQuantity(quantity);
        inventory.setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        inventoryRepository.save(inventory);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Cập nhật tồn kho thành công")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllProductVariantByProductId(int productId) {
        // Check product
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("ProductID: " + productId + " không tồn tại")
                            .build());
        }

        ProductResponse productResponse = ProductResponse.builder()
                .name(product.get().getName())
                .price(product.get().getPrice())
                .description(product.get().getDescription())
                .imagePath(product.get().getImagePath())
                .isActive(product.get().getIsActive())
                .build();

        List<ProductVariant> productVariants = product.get().getProductVariants();

        List<ProductVariantResponse> productVariantResponses = productVariants.stream()
                .map(productVariant -> ProductVariantResponse.builder()
                        .productId(productId)
                        .productVariantId(productVariant.getProductVariantId())
                        .variantName(productVariant.getVariantName())
                        .sellingPrice(productVariant.getSellingPrice())
                        .isActive(productVariant.isActive())
                        .inventory(productVariant.getInventory().getQuantity())
                        .imageVariants(productVariant.getVariantImageGroup().getImageVariants().stream()
                                .map(imageVariant -> ImageVariantResponse.builder()
                                        .imagePath(imageVariant.getImagePath())
                                        .build())
                                .toList()
                        )
                        .build())
                .toList();

        // Lấy attributes
        List<AttributeValue> attributeValues = productVariants.stream()
                .flatMap(pv -> pv.getVariantAttributes().stream())
                .map(VariantAttribute::getAttributeValue)
                .distinct()
                .toList();


        // Response về attributes
        Map<String, List<AttributeValueResponse>> attributes = attributeValues.stream()
                .collect(Collectors.groupingBy(
                        av -> av.getAttribute().getType(),
                        Collectors.mapping(
                                av -> AttributeValueResponse.builder()
                                        .attributeValueId(av.getAttributeValueId())
                                        .value(av.getValue())
                                        .hexCode(av.getHexCode())
                                        .build(),
                                Collectors.toList()
                        )
                ));


        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Lấy danh sách biến thể theo productID: " + productId)
                        .data(Map.of(
                                "productId", productId,
                                "description", productResponse.getDescription(),
                                "variants", productVariantResponses,
                                "attributes", attributes
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateStatusProductVariants(int variantId) {
        // Check product variant
        Optional<ProductVariant> productVariant = productVariantRepository.findById(variantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Sản phẩm biến thể với ID: " + variantId + " không tồn tại")
                            .build());
        }

        // Thay đổi trạng thái
        boolean currentStatus = productVariant.get().isActive();
        productVariant.get().setActive(!currentStatus);
        productVariantRepository.save(productVariant.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật trạng thái thành công")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> selectAttribute(int productId, List<Integer> attributeValueIds) {
        // Check product
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Sản phẩm với ID: " + productId + " không tồn tại")
                            .build());
        }


        Optional<ProductVariant> productVariant = productVariantRepository.findVariantsByProductIdAndAttributeValue(
                productId, attributeValueIds, attributeValueIds.size()
        );
        if (productVariant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Biến thể sản phẩm không tồn tại.")
                            .build());
        }

        // Lấy tất cả các ảnh của biến thể
        List<ImageVariantResponse> imageVariantResponses = productVariant.get().getVariantImageGroup().getImageVariants().stream()
                .map(imageVariant -> ImageVariantResponse.builder()
                        .imagePath(imageVariant.getImagePath())
                        .build())
                .toList();

        // lấy ra thông tin attribute
        List<AttributeValueResponse> attributes = productVariant.get().getVariantAttributes().stream()
                .map(va -> AttributeValueResponse.builder()
                        .attributeValueId(va.getAttributeValue().getAttributeValueId())
                        .value(va.getAttributeValue().getValue())
                        .hexCode(va.getAttributeValue().getHexCode())
                        .build())
                .toList();


        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Biến thể phù hợp với các thuộc tính đã chọn")
                        .data(Map.of(
                                "productId", productId,
                                "productVariantId", productVariant.get().getProductVariantId(),
                                "sellingPrice", productVariant.get().getSellingPrice(),
                                "inventory", productVariant.get().getInventory().getQuantity(),
                                "attributes", attributes,
                                "images", imageVariantResponses
                        ))
                        .build());
    }
}
