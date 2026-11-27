package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.ProductRequest;
import com.example.clothes_website.dto.request.ProductSearchRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductDetailResponse;
import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.service.ProductDetailService;
import com.example.clothes_website.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ProductDetailService productDetailService;

    @Operation(
            summary = "Lấy chi tiết sản phẩm",
            description = "Trả về thông tin sản phẩm, media, options và các variant đang hoạt động."
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(
            @PathVariable int productId
    ) {
        return ResponseEntity.ok(ApiResponse.<ProductDetailResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy chi tiết sản phẩm thành công")
                .data(productDetailService.getProductDetail(productId))
                .build());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestPart ProductRequest productRequest,
            @RequestPart MultipartFile image
    ) {
        ProductResponse productResponse = productService.addProduct(productRequest, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProductResponse>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Thêm sản phẩm thành công")
                .data(productResponse)
                .build());
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable int productId,
            @Valid @RequestPart ProductRequest productRequest,
            @RequestPart(required = false) MultipartFile image
    ) {
        ProductResponse productResponse = productService.updateProduct(productId, productRequest, image);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật sản phẩm thành công")
                .data(productResponse)
                .build());
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable int productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Xóa sản phẩm thành công")
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @Valid @ModelAttribute ProductSearchRequest request
    ) {
        Page<ProductResponse> productResponses = productService.getProducts(request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<ProductResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách sản phẩm")
                .data(productResponses)
                .build());
    }

    @PutMapping("/{productId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStatus(@PathVariable int productId) {
        ProductResponse productResponse = productService.updateProductStatus(productId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật trạng thái sản phẩm thành công")
                .data(productResponse)
                .build());
    }
}
