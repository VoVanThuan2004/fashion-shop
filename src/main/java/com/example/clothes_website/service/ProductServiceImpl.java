package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.cloudinary.UploadResult;
import com.example.clothes_website.dto.request.AdminProductSearchRequest;
import com.example.clothes_website.dto.request.ProductRequest;
import com.example.clothes_website.dto.request.ProductSearchRequest;
import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.entity.Brand;
import com.example.clothes_website.entity.Category;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.ProductMapper;
import com.example.clothes_website.repository.BrandRepository;
import com.example.clothes_website.repository.CategoryRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "price", "createdAt");

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse addProduct(ProductRequest productRequest, MultipartFile image) {
        Category category = getCategoryById(productRequest.getCategoryId());

        Brand brand = getBrandById(productRequest.getBrandId());

        // Check image
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Vui lòng upload ảnh cho sản phẩm");
        }

        // Lấy đường dẫn image
        String imagePath = "";
        String cloudinaryPublicId = "";
        try {
            UploadResult uploadResult = cloudinaryService.uploadCloudinaryImage(image);
            imagePath = uploadResult.getUrl();
            cloudinaryPublicId = uploadResult.getPublicId();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .imagePath(imagePath)
                .cloudinaryPublicId(cloudinaryPublicId)
                .category(category)
                .brand(brand)
                .build();

        productRepository.save(product);

        return productMapper.mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(int productId, ProductRequest productRequest, MultipartFile image) {
        Product product = getProductById(productId);

        // Check category, brand
        Category category = getCategoryById(productRequest.getCategoryId());

        Brand brand = getBrandById(productRequest.getBrandId());

        // Check image nếu có update
        if (image != null) {
            try {
                // Xóa image cũ
                cloudinaryService.deleteImage(product.getCloudinaryPublicId());

                UploadResult uploadResult = cloudinaryService.uploadCloudinaryImage(image);
                product.setImagePath(uploadResult.getUrl());
                product.setCloudinaryPublicId(uploadResult.getPublicId());
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        productRepository.save(product);

        return productMapper.mapToResponse(product);
    }

    @Override
    public void deleteProduct(int productId) {
        Product product = getProductById(productId);

        // Xóa mềm product
        product.setIsDeleted(true);

        productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> getProducts(ProductSearchRequest request) {
        validatePriceRange(request.getMinPrice(), request.getMaxPrice());
        validateParentCategory(request.getParentCategoryId());

        String sortField = validateSortField(request.getSortBy());
        Sort.Direction direction = validateDirection(request.getDirection());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(direction, sortField));
        Page<Product> products = productRepository.findAll(ProductSpecification.publicFilter(request), pageable);

        return products.map(productMapper::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsForAdmin(AdminProductSearchRequest request) {
        validatePriceRange(request.getMinPrice(), request.getMaxPrice());
        validateParentCategory(request.getParentCategoryId());

        String sortField = validateSortField(request.getSortBy());
        Sort.Direction direction = validateDirection(request.getDirection());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(direction, sortField));
        Page<Product> products = productRepository.findAll(ProductSpecification.adminFilter(request), pageable);

        return products.map(productMapper::mapToResponse);
    }

    @Override
    public ProductResponse updateProductStatus(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        product.setIsActive(!Boolean.TRUE.equals(product.getIsActive()));
        productRepository.save(product);

        return productMapper.mapToResponse(product);
    }

    private void validatePriceRange(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new BadRequestException("Giá tối thiểu không được lớn hơn giá tối đa");
        }
    }

    private void validateParentCategory(Integer parentCategoryId) {
        if (parentCategoryId == null) {
            return;
        }

        Category category = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));

        if (category.getCategoryParent() != null) {
            throw new BadRequestException("Danh mục không phải danh mục cha");
        }
    }

    private String validateSortField(String sortBy) {
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException("Trường sắp xếp không hợp lệ: " + sortBy);
        }
        return sortBy;
    }

    private Sort.Direction validateDirection(String direction) {
        if ("asc".equalsIgnoreCase(direction)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.Direction.DESC;
        }
        throw new BadRequestException("Chiều sắp xếp không hợp lệ: " + direction);
    }


    // Hàm verify product theo id
    private Product getProductById(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
    }

    private Category getCategoryById(int categoryId) {
        return categoryRepository.findByCategoryIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));
    }

    private Brand getBrandById(int brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu không tồn tại"));
    }
}
