package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.ProductMediaUpdateRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductMediaResponse;
import com.example.clothes_website.service.ProductMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductMediaController {
    private final ProductMediaService productMediaService;

    @GetMapping("/products/{productId}/media")
    public ResponseEntity<ApiResponse<List<ProductMediaResponse>>> getProductMedia(
            @PathVariable int productId
    ) {
        return ResponseEntity.ok(ApiResponse.<List<ProductMediaResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách media của sản phẩm thành công")
                .data(productMediaService.getProductMedia(productId))
                .build());
    }

    @Operation(
            summary = "Upload một media cho sản phẩm",
            description = "Upload một file ảnh bằng multipart/form-data."
    )
    @PostMapping(value = "/products/{productId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductMediaResponse>> uploadMedia(
            @PathVariable int productId,
            @Parameter(
                    description = "File ảnh cần upload",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "IMAGE") String mediaType,
            @RequestParam(required = false) String altText
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductMediaResponse>builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Upload media thành công")
                        .data(productMediaService.uploadMedia(productId, file, mediaType, altText))
                        .build());
    }

    @PutMapping("/product-media/{mediaId}")
    public ResponseEntity<ApiResponse<ProductMediaResponse>> updateMedia(
            @PathVariable int mediaId,
            @Valid @RequestBody ProductMediaUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.<ProductMediaResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật media thành công")
                .data(productMediaService.updateMedia(mediaId, request))
                .build());
    }

    @DeleteMapping("/product-media/{mediaId}")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable int mediaId) {
        productMediaService.deleteMedia(mediaId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Xóa media thành công")
                .build());
    }

}
