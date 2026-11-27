package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.dto.request.GenerateVariantsRequest;
import com.example.clothes_website.dto.request.ProductVariantCreateRequest;
import com.example.clothes_website.dto.request.ProductVariantUpdateRequest;
import com.example.clothes_website.dto.request.UpdateInventoryRequest;
import com.example.clothes_website.dto.response.ProductVariantDetailResponse;
import com.example.clothes_website.entity.Inventory;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.ProductOption;
import com.example.clothes_website.entity.ProductOptionValue;
import com.example.clothes_website.entity.ProductVariant;
import com.example.clothes_website.entity.ProductVariantImage;
import com.example.clothes_website.entity.ProductVariantOptionValue;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.ProductVariantMapper;
import com.example.clothes_website.repository.InventoryRepository;
import com.example.clothes_website.repository.ProductOptionRepository;
import com.example.clothes_website.repository.ProductOptionValueRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductVariantServiceImpl implements AdminProductVariantService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final InventoryRepository inventoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductVariantMapper productVariantMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDetailResponse> getVariants(int productId) {
        getProduct(productId);

        return productVariantRepository.findByProductProductId(productId).stream()
                .map(productVariantMapper::mapToDetail)
                .toList();
    }

    @Override
    public ProductVariantDetailResponse createVariant(int productId, ProductVariantCreateRequest request) {
        Product product = getProduct(productId);
        validatePrices(request.getOriginalPrice(), request.getSellingPrice());

        List<ProductOptionValue> selectedValues = resolveOptionValues(product, request.getOptionValueIds());
        String signature = buildSignature(selectedValues);

        if (productVariantRepository.existsByProductProductIdAndOptionSignature(productId, signature)) {
            throw new BadRequestException("Biến thể với các lựa chọn này đã tồn tại");
        }

        String sku = normalizeSku(request.getSku());
        if (sku != null && productVariantRepository.existsBySku(sku)) {
            throw new BadRequestException("SKU đã tồn tại");
        }

        ProductVariant variant = buildVariant(
                product, selectedValues, sku, signature,
                request.getOriginalPrice(), request.getSellingPrice()
        );
        productVariantRepository.save(variant);

        Inventory inventory = saveInventory(variant, request.getInventoryQuantity());
        variant.setInventory(inventory);

        updateProductPrice(productId);

        return productVariantMapper.mapToDetail(variant);
    }

    @Override
    public List<ProductVariantDetailResponse> generateVariants(int productId, GenerateVariantsRequest request) {
        Product product = getProduct(productId);
        validatePrices(request.getOriginalPrice(), request.getSellingPrice());

        List<List<ProductOptionValue>> combinations = buildCombinations(product);

        List<ProductVariant> createdVariants = new ArrayList<>();
        for (List<ProductOptionValue> combination : combinations) {
            String signature = buildSignature(combination);

            if (productVariantRepository.existsByProductProductIdAndOptionSignature(productId, signature)) {
                continue;
            }

            ProductVariant variant = buildVariant(
                    product, combination, null, signature,
                    request.getOriginalPrice(), request.getSellingPrice()
            );
            productVariantRepository.save(variant);

            Inventory inventory = saveInventory(variant, request.getInventoryQuantity());
            variant.setInventory(inventory);

            createdVariants.add(variant);
        }

        if (!createdVariants.isEmpty()) {
            updateProductPrice(productId);
        }

        return createdVariants.stream()
                .map(productVariantMapper::mapToDetail)
                .toList();
    }

    @Override
    public ProductVariantDetailResponse updateVariant(int variantId, ProductVariantUpdateRequest request) {
        ProductVariant variant = getVariant(variantId);
        Product product = variant.getProduct();
        validatePrices(request.getOriginalPrice(), request.getSellingPrice());

        List<Integer> optionValueIds = request.getOptionValueIds();
        if (optionValueIds == null) {
            optionValueIds = variant.getVariantOptionValues().stream()
                    .map(item -> item.getOptionValue().getOptionValueId())
                    .toList();
        }

        List<ProductOptionValue> selectedValues = resolveOptionValues(product, optionValueIds);
        String signature = buildSignature(selectedValues);

        if (productVariantRepository.existsByProductProductIdAndOptionSignatureAndProductVariantIdNot(
                product.getProductId(), signature, variantId)) {
            throw new BadRequestException("Biến thể với các lựa chọn này đã tồn tại");
        }

        String sku = normalizeSku(request.getSku());
        if (sku != null && productVariantRepository.existsBySkuAndProductVariantIdNot(sku, variantId)) {
            throw new BadRequestException("SKU đã tồn tại");
        }

        variant.setSku(sku);
        variant.setOriginalPrice(request.getOriginalPrice());
        variant.setSellingPrice(request.getSellingPrice());
        variant.setOptionSignature(signature);
        variant.setVariantName(buildVariantName(product, selectedValues));
        variant.setUpdateTime(LocalDateTime.now(ZONE));

        Set<Integer> currentIds = variant.getVariantOptionValues().stream()
                .map(item -> item.getOptionValue().getOptionValueId())
                .collect(Collectors.toSet());
        Set<Integer> selectedIds = selectedValues.stream()
                .map(ProductOptionValue::getOptionValueId)
                .collect(Collectors.toSet());

        if (!currentIds.equals(selectedIds)) {
            variant.getVariantOptionValues().clear();
            for (ProductOptionValue value : selectedValues) {
                variant.getVariantOptionValues().add(ProductVariantOptionValue.builder()
                        .productVariant(variant)
                        .optionValue(value)
                        .build());
            }
        }

        productVariantRepository.save(variant);
        updateProductPrice(product.getProductId());

        return productVariantMapper.mapToDetail(variant);
    }

    @Override
    public void deleteVariant(int variantId) {
        ProductVariant variant = getVariant(variantId);
        int productId = variant.getProduct().getProductId();

        for (ProductVariantImage image : variant.getVariantImages()) {
            if (image.getPublicId() != null) {
                cloudinaryService.deleteImage(image.getPublicId());
            }
        }

        productVariantRepository.delete(variant);
        updateProductPrice(productId);
    }

    @Override
    public ProductVariantDetailResponse updateInventory(int variantId, UpdateInventoryRequest request) {
        ProductVariant variant = getVariant(variantId);

        Inventory inventory = variant.getInventory();
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductVariant(variant);
            inventory.setCreateTime(LocalDateTime.now(ZONE));
        }

        inventory.setQuantity(request.getQuantity());
        inventory.setUpdateTime(LocalDateTime.now(ZONE));
        inventoryRepository.save(inventory);
        variant.setInventory(inventory);

        return productVariantMapper.mapToDetail(variant);
    }

    @Override
    public ProductVariantDetailResponse updateStatus(int variantId) {
        ProductVariant variant = getVariant(variantId);
        variant.setActive(!variant.isActive());
        variant.setUpdateTime(LocalDateTime.now(ZONE));
        productVariantRepository.save(variant);

        return productVariantMapper.mapToDetail(variant);
    }

    private ProductVariant buildVariant(
            Product product,
            List<ProductOptionValue> selectedValues,
            String sku,
            String signature,
            double originalPrice,
            double sellingPrice
    ) {
        LocalDateTime now = LocalDateTime.now(ZONE);

        ProductVariant variant = ProductVariant.builder()
                .variantName(buildVariantName(product, selectedValues))
                .sku(sku)
                .optionSignature(signature)
                .originalPrice(originalPrice)
                .sellingPrice(sellingPrice)
                .isActive(true)
                .createTime(now)
                .updateTime(now)
                .product(product)
                .build();

        for (ProductOptionValue value : selectedValues) {
            variant.getVariantOptionValues().add(ProductVariantOptionValue.builder()
                    .productVariant(variant)
                    .optionValue(value)
                    .build());
        }

        return variant;
    }

    private List<List<ProductOptionValue>> buildCombinations(Product product) {
        List<ProductOption> options = productOptionRepository.findByProductProductIdOrderByPositionAsc(product.getProductId());

        if (options.isEmpty()) {
            List<List<ProductOptionValue>> defaultCombination = new ArrayList<>();
            defaultCombination.add(List.of());
            return defaultCombination;
        }

        List<List<ProductOptionValue>> combinations = new ArrayList<>();
        combinations.add(new ArrayList<>());

        for (ProductOption option : options) {
            List<ProductOptionValue> values = option.getValues().stream()
                    .sorted(Comparator.comparingInt(ProductOptionValue::getPosition))
                    .toList();

            if (values.isEmpty()) {
                throw new BadRequestException("Option '" + option.getName() + "' chưa có giá trị");
            }

            List<List<ProductOptionValue>> next = new ArrayList<>();
            for (List<ProductOptionValue> combination : combinations) {
                for (ProductOptionValue value : values) {
                    List<ProductOptionValue> newCombination = new ArrayList<>(combination);
                    newCombination.add(value);
                    next.add(newCombination);
                }
            }
            combinations = next;
        }

        return combinations;
    }

    private List<ProductOptionValue> resolveOptionValues(Product product, List<Integer> optionValueIds) {
        List<ProductOption> options = productOptionRepository.findByProductProductIdOrderByPositionAsc(product.getProductId());
        List<Integer> ids = optionValueIds == null ? List.of() : optionValueIds;

        if (ids.stream().distinct().count() != ids.size()) {
            throw new BadRequestException("Option value bị trùng");
        }

        if (options.isEmpty()) {
            if (!ids.isEmpty()) {
                throw new BadRequestException("Sản phẩm không có option, không được chọn option value");
            }
            return List.of();
        }

        if (ids.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn đầy đủ option value");
        }

        List<ProductOptionValue> values = productOptionValueRepository.findAllById(ids);
        if (values.size() != ids.size()) {
            throw new ResourceNotFoundException("Option value không tồn tại");
        }

        Set<Integer> optionIds = new HashSet<>();
        for (ProductOptionValue value : values) {
            if (value.getOption() == null
                    || value.getOption().getProduct() == null
                    || value.getOption().getProduct().getProductId() != product.getProductId()) {
                throw new BadRequestException("Option value không thuộc sản phẩm");
            }
            if (!optionIds.add(value.getOption().getOptionId())) {
                throw new BadRequestException("Mỗi option chỉ được chọn 1 giá trị");
            }
        }

        if (optionIds.size() != options.size()) {
            throw new BadRequestException("Vui lòng chọn đầy đủ option value");
        }

        return values;
    }

    private String buildSignature(List<ProductOptionValue> values) {
        if (values.isEmpty()) {
            return "default";
        }

        return values.stream()
                .mapToInt(ProductOptionValue::getOptionValueId)
                .sorted()
                .mapToObj(String::valueOf)
                .collect(Collectors.joining("-"));
    }

    private String buildVariantName(Product product, List<ProductOptionValue> values) {
        if (values.isEmpty()) {
            return product.getName();
        }

        String optionText = values.stream()
                .sorted(Comparator.comparingInt((ProductOptionValue value) -> value.getOption().getPosition()))
                .map(ProductOptionValue::getValue)
                .collect(Collectors.joining(" - "));

        return product.getName() + " " + optionText;
    }

    private String normalizeSku(String sku) {
        if (sku == null) {
            return null;
        }
        String trimmed = sku.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validatePrices(Double originalPrice, Double sellingPrice) {
        if (originalPrice >= sellingPrice) {
            throw new BadRequestException("Giá gốc phải nhỏ hơn giá bán");
        }
    }

    private Inventory saveInventory(ProductVariant variant, int quantity) {
        LocalDateTime now = LocalDateTime.now(ZONE);

        Inventory inventory = new Inventory();
        inventory.setQuantity(quantity);
        inventory.setCreateTime(now);
        inventory.setUpdateTime(now);
        inventory.setProductVariant(variant);

        return inventoryRepository.save(inventory);
    }

    private void updateProductPrice(int productId) {
        Product product = getProduct(productId);

        double minSellingPrice = productVariantRepository.findByProductProductId(productId).stream()
                .mapToDouble(ProductVariant::getSellingPrice)
                .min()
                .orElse(0);

        product.setPrice(minSellingPrice);
        productRepository.save(product);
    }

    private Product getProduct(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
    }

    private ProductVariant getVariant(int variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm biến thể không tồn tại"));
    }
}
