package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.ProductOptionRequest;
import com.example.clothes_website.dto.request.ProductOptionValueRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductOptionResponse;
import com.example.clothes_website.dto.response.ProductOptionValueResponse;
import com.example.clothes_website.service.ProductOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products/{productId}/options")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductOptionController {
    private final ProductOptionService productOptionService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ProductOptionResponse>>> getOptions(@PathVariable int productId) {
        List<ProductOptionResponse> options = productOptionService.getOptions(productId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<List<ProductOptionResponse>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách option của sản phẩm")
                .data(options)
                .build());
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> createOption(
            @PathVariable int productId,
            @Valid @RequestBody ProductOptionRequest request
    ) {
        ProductOptionResponse option = productOptionService.createOption(productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProductOptionResponse>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Tạo option thành công")
                .data(option)
                .build());
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> updateOption(
            @PathVariable int productId,
            @PathVariable int optionId,
            @Valid @RequestBody ProductOptionRequest request
    ) {
        ProductOptionResponse option = productOptionService.updateOption(productId, optionId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductOptionResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật option thành công")
                .data(option)
                .build());
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<ApiResponse<?>> deleteOption(
            @PathVariable int productId,
            @PathVariable int optionId
    ) {
        productOptionService.deleteOption(productId, optionId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.builder()
                .status("success")
                .code(HttpStatus.NO_CONTENT.value())
                .message("Xoá option thành công")
                .build());
    }

    @PostMapping("/{optionId}/values")
    public ResponseEntity<ApiResponse<ProductOptionValueResponse>> createValue(
            @PathVariable int productId,
            @PathVariable int optionId,
            @Valid @RequestBody ProductOptionValueRequest request
    ) {
        ProductOptionValueResponse value = productOptionService.createValue(productId, optionId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProductOptionValueResponse>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message("Thêm giá trị option thành công")
                .data(value)
                .build());
    }

    @PutMapping("/{optionId}/values/{valueId}")
    public ResponseEntity<ApiResponse<ProductOptionValueResponse>> updateValue(
            @PathVariable int productId,
            @PathVariable int optionId,
            @PathVariable int valueId,
            @Valid @RequestBody ProductOptionValueRequest request
    ) {
        ProductOptionValueResponse value = productOptionService.updateValue(productId, optionId, valueId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductOptionValueResponse>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message("Cập nhật giá trị option thành công")
                .data(value)
                .build());
    }

    @DeleteMapping("/{optionId}/values/{valueId}")
    public ResponseEntity<ApiResponse<?>> deleteValue(
            @PathVariable int productId,
            @PathVariable int optionId,
            @PathVariable int valueId
    ) {
        productOptionService.deleteValue(productId, optionId, valueId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.builder()
                .status("success")
                .code(HttpStatus.NO_CONTENT.value())
                .message("Xoá giá trị option thành công")
                .build());
    }
}
