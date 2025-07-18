package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.ProductRequest;
import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.entity.Brand;
import com.example.clothes_website.entity.Category;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.repository.BrandRepository;
import com.example.clothes_website.repository.CategoryRepository;
import com.example.clothes_website.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> addProduct(ProductRequest productRequest, MultipartFile image) {
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Category not found")
                            .build());
        }

        Optional<Brand> brand = brandRepository.findById(productRequest.getBrandId());
        if (brand.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Brand not found")
                            .build());
        }

        // Check image
        if (image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Please upload image for product")
                            .build());
        }

        // Lấy đường dẫn image
        String imagePath = "";
        try {
            imagePath = cloudinaryService.uploadImage(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message(e.getMessage())
                            .build());
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .createTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                .isActive(false)
                .imagePath(imagePath)
                .category(category.get())
                .brand(brand.get())
                .build();

        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Product added successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> uploadProduct(int productId, ProductRequest productRequest, MultipartFile image) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Product is not found")
                            .build());
        }

        // Check category, brand
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Category not found")
                            .build());
        }

        Optional<Brand> brand = brandRepository.findById(productRequest.getBrandId());
        if (brand.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Brand not found")
                            .build());
        }

        // Check image nếu có update
        String imagePath = "";
        if (image != null) {
            try {
                imagePath = cloudinaryService.uploadImage(image);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message(e.getMessage())
                                .build());
            }
        }

        product.get().setName(productRequest.getName());
        product.get().setDescription(productRequest.getDescription());
        product.get().setCategory(category.get());
        product.get().setBrand(brand.get());
        product.get().setImagePath(Objects.equals(imagePath, "") ? product.get().getImagePath() : imagePath);
        product.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        productRepository.save(product.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Product updated successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteProduct(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Product is not found")
                            .build());
        }

        productRepository.delete(product.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Product deleted successfully")
                        .build());
    }

    @Override
    public ApiResponse<?> getAllProducts(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createTime").descending()
        );

        Page<Product> products = productRepository.findAll(sortedPageable);

        // Chuyển sang response
        Page<ProductResponse> productResponses = products
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imagePath(product.getImagePath())
                        .isActive(product.getIsActive())
                        .createTime(product.getCreateTime())
                        .updateTime(product.getUpdateTime())
                        .build());

        return ApiResponse.builder()
                .status("success")
                .code(200)
                .message("Get all products successfully")
                .data(productResponses)
                .build();
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateProductStatus(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Product is not found")
                            .build());
        }

        // Thay đổi trạng thái
        if (product.get().getIsActive()) {
            product.get().setIsActive(false);
            productRepository.save(product.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Product locked")
                            .build());
        }

        product.get().setIsActive(true);
        product.get().setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        productRepository.save(product.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Product unlocked")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> filterProducts(int page, int size, List<Integer> categoryIds, List<Integer> brandIds, Double minPrice, Double maxPrice) {
        Pageable sortedPageable = PageRequest.of(
                page,
                size
        );

        Page<Product> products;
        if (categoryIds == null && brandIds == null && minPrice == null && maxPrice == null) {
            products = productRepository.findAll(sortedPageable);
        }

        else {
            if (categoryIds != null) {
                for (Integer categoryId : categoryIds) {
                    Optional<Category> category = categoryRepository.findById(categoryId);
                    if (category.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.builder()
                                        .status("error")
                                        .code(404)
                                        .message("Category: " + categoryId + " is not found")
                                        .build());
                    }
                }
            }

            if (brandIds != null) {
                for (Integer brandId : brandIds) {
                    Optional<Brand> brand = brandRepository.findById(brandId);
                    if (brand.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.builder()
                                        .status("error")
                                        .code(404)
                                        .message("Brand: " + brandId + " is not found")
                                        .build());
                    }
                }
            }

            // Lọc product
            products = productRepository.filterProduct(sortedPageable, categoryIds, brandIds, minPrice, maxPrice);
        }

        // Chuyển sang response
        Page<ProductResponse> productResponses = products
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imagePath(product.getImagePath())
                        .isActive(product.getIsActive())
                        .build());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Filter products")
                        .data(productResponses)
                        .build());
    }

    @Override
    public ApiResponse<?> searchProducts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.searchProductsByName(pageable, keyword);

        Page<ProductResponse> productResponses = products
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imagePath(product.getImagePath())
                        .isActive(product.getIsActive())
                        .createTime(product.getCreateTime())
                        .updateTime(product.getUpdateTime())
                        .build());

        return ApiResponse.builder()
                .status("success")
                .code(200)
                .message("Search products")
                .data(productResponses)
                .build();
    }

    @Override
    public ApiResponse<?> sortProducts(int page, int size, String filter, String sort) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products;
        // Sort theo name
        if (filter.equals("name")) {
            if (sort.equals("asc")) {
                products = productRepository.sortProductsByNameAsc(pageable);
            }
            else {
                products = productRepository.sortProductsByNameDesc(pageable);
            }
        }
        else {
            if (sort.equals("asc")) {
                products = productRepository.sortProductsByPriceAsc(pageable);
            }
            else {
                products = productRepository.sortProductsByPriceDesc(pageable);
            }
        }

        Page<ProductResponse> productResponses = products
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imagePath(product.getImagePath())
                        .isActive(product.getIsActive())
                        .createTime(product.getCreateTime())
                        .updateTime(product.getUpdateTime())
                        .build());


        return ApiResponse.builder()
                .status("success")
                .code(200)
                .message("Sort products")
                .data(productResponses)
                .build();
    }

    @Override
    public ApiResponse<?> getAllProductsBelongToCategoryParent(int categoryId, Pageable pageable) {
        // Check có phải là category cha không (root)
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return ApiResponse.builder()
                    .status("error")
                    .code(404)
                    .message("Category: " + categoryId + " is not found")
                    .build();
        }

        if (category.get().getCategoryParent() != null) {
            return ApiResponse.builder()
                    .status("error")
                    .code(400)
                    .message("Category: " + categoryId + " is not category parent")
                    .build();
        }

        Page<Product> products = productRepository.findAllProductsBelongToCategoryParent(categoryId, pageable);

        Page<ProductResponse> productResponses = products
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imagePath(product.getImagePath())
                        .isActive(product.getIsActive())
                        .createTime(product.getCreateTime())
                        .updateTime(product.getUpdateTime())
                        .build());

        return ApiResponse.builder()
                .status("success")
                .code(200)
                .message("Get all products belong to category parent")
                .data(productResponses)
                .build();
    }
}
