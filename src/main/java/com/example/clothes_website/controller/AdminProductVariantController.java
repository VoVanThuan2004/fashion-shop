package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.GenerateVariantsRequest;
import com.example.clothes_website.dto.request.AssignProductVariantMediaRequest;
import com.example.clothes_website.dto.request.ProductVariantCreateRequest;
import com.example.clothes_website.dto.request.ProductVariantUpdateRequest;
import com.example.clothes_website.dto.request.UpdateInventoryRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductVariantDetailResponse;
import com.example.clothes_website.dto.response.ProductVariantMediaResponse;
import com.example.clothes_website.service.AdminProductVariantService;
import com.example.clothes_website.service.ProductVariantMediaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductVariantController {
    private final AdminProductVariantService adminProductVariantService;
    private final ProductVariantMediaService productVariantMediaService;

    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<ApiResponse<List<ProductVariantDetailResponse>>> getVariants(@PathVariable int productId) {
        List<ProductVariantDetailResponse> variants = adminProductVariantService.getVariants(productId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<List<ProductVariantDetailResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách biến thể của sản phẩm")
                .data(variants)
                .build());
    }

    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> createVariant(
            @PathVariable int productId,
            @Valid @RequestBody ProductVariantCreateRequest request
    ) {
        ProductVariantDetailResponse variant = adminProductVariantService.createVariant(productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProductVariantDetailResponse>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Tạo biến thể sản phẩm thành công")
                .data(variant)
                .build());
    }

    @PostMapping("/products/{productId}/variants/generate")
    public ResponseEntity<ApiResponse<List<ProductVariantDetailResponse>>> generateVariants(
            @PathVariable int productId,
            @Valid @RequestBody GenerateVariantsRequest request
    ) {
        List<ProductVariantDetailResponse> variants = adminProductVariantService.generateVariants(productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<ProductVariantDetailResponse>>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Tạo biến thể sản phẩm thành công")
                .data(variants)
                .build());
    }

    @PutMapping("/product-variants/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> updateVariant(
            @PathVariable int variantId,
            @Valid @RequestBody ProductVariantUpdateRequest request
    ) {
        ProductVariantDetailResponse variant = adminProductVariantService.updateVariant(variantId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductVariantDetailResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật biến thể sản phẩm thành công")
                .data(variant)
                .build());
    }

    @DeleteMapping("/product-variants/{variantId}")
    public ResponseEntity<ApiResponse<?>> deleteVariant(@PathVariable int variantId) {
        adminProductVariantService.deleteVariant(variantId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.builder()
                .status("success")
                .code(HttpStatus.NO_CONTENT.value())
                .message("Xoá biến thể sản phẩm thành công")
                .build());
    }

    @PutMapping("/product-variants/{variantId}/inventory")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> updateInventory(
            @PathVariable int variantId,
            @Valid @RequestBody UpdateInventoryRequest request
    ) {
        ProductVariantDetailResponse variant = adminProductVariantService.updateInventory(variantId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductVariantDetailResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật tồn kho thành công")
                .data(variant)
                .build());
    }

    @PutMapping("/product-variants/{variantId}/status")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> updateStatus(@PathVariable int variantId) {
        ProductVariantDetailResponse variant = adminProductVariantService.updateStatus(variantId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductVariantDetailResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật trạng thái biến thể thành công")
                .data(variant)
                .build());
    }

    @Operation(
            summary = "Lấy media của product variant",
            description = "Trả về các media đã được gắn vào variant theo thứ tự position."
    )
    @GetMapping("/product-variants/{variantId}/media")
    public ResponseEntity<ApiResponse<List<ProductVariantMediaResponse>>> getVariantMedia(
            @PathVariable int variantId
    ) {
        return ResponseEntity.ok(ApiResponse.<List<ProductVariantMediaResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy media của biến thể thành công")
                .data(productVariantMediaService.getVariantMedia(variantId))
                .build());
    }

    @Operation(
            summary = "Chọn media cho product variant",
            description = "Thay thế toàn bộ danh sách media của variant bằng các media thuộc product cha. " +
                    "Thứ tự mediaIds là thứ tự hiển thị; nếu không truyền featuredMediaId thì media đầu tiên là ảnh đại diện."
    )
    @PutMapping("/product-variants/{variantId}/media")
    public ResponseEntity<ApiResponse<List<ProductVariantMediaResponse>>> assignMedia(
            @PathVariable int variantId,
            @Valid @RequestBody AssignProductVariantMediaRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.<List<ProductVariantMediaResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Gắn media vào biến thể thành công")
                .data(productVariantMediaService.assignMedia(variantId, request))
                .build());
    }

    @Operation(
            summary = "Gỡ media khỏi product variant",
            description = "Chỉ xóa liên kết với variant, không xóa ProductMedia hoặc file trên Cloudinary."
    )
    @DeleteMapping("/product-variants/{variantId}/media/{mediaId}")
    public ResponseEntity<ApiResponse<Void>> removeMedia(
            @PathVariable int variantId,
            @PathVariable int mediaId
    ) {
        productVariantMediaService.removeMedia(variantId, mediaId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Gỡ media khỏi biến thể thành công")
                .build());
    }
}
