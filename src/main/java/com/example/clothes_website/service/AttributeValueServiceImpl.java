package com.example.clothes_website.service;

import com.cloudinary.Api;
import com.example.clothes_website.dto.request.AttributeValueRequest;
import com.example.clothes_website.dto.request.AttributeValueUpdate;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.AttributeValueResponse;
import com.example.clothes_website.entity.Attribute;
import com.example.clothes_website.entity.AttributeValue;
import com.example.clothes_website.repository.AttributeRepository;
import com.example.clothes_website.repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeValueServiceImpl implements AttributeValueService {
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeRepository attributeRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> addAttributeValue(AttributeValueRequest attributeValueRequest) {
        // Check attribute has exists or not
        Optional<Attribute> attribute = attributeRepository.findById(attributeValueRequest.getAttributeId());
        if (attribute.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Thuộc tính không tồn tại")
                            .build());
        }

        // Check attribute này là màu sắc -> thì phải có hexCode mã màu
        if (attribute.get().getType().equals("color")) {
            if (attributeValueRequest.getHexCode() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message("Mã HexCode bị trống")
                                .build());
            }
        }

        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setValue(attributeValueRequest.getValue());
        attributeValue.setHexCode(attributeValueRequest.getHexCode());
        attributeValue.setAttribute(attribute.get());
        attributeValueRepository.save(attributeValue);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Giá trị thuộc tính đã thêm thành công")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateAttributeValue(int attributeValueId, AttributeValueUpdate attributeValueUpdate) {
        // Check attributeValue có tồn tại hay kh
        Optional<AttributeValue> attributeValue = attributeValueRepository.findById(attributeValueId);
        if (attributeValue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Attribute Value: " + attributeValueId + " không tồn tại")
                            .build());
        }

        // Check attribute này là màu sắc -> thì phải có hexCode mã màu
        if (attributeValue.get().getAttribute().getType().equals("color")) {
            if (attributeValueUpdate.getHexCode() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message("Mã HexCode bị trống")
                                .build());
            }
        }

        attributeValue.get().setHexCode(attributeValueUpdate.getHexCode());
        attributeValue.get().setValue(attributeValueUpdate.getValue());
        attributeValueRepository.save(attributeValue.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Giá trị thuộc tính đã cập nhật")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteAttributeValue(int attributeValueId) {
        // Check attributeValue có tồn tại hay kh
        Optional<AttributeValue> attributeValue = attributeValueRepository.findById(attributeValueId);
        if (attributeValue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Attribute Value: " + attributeValueId + " không tồn tại")
                            .build());
        }

        attributeValueRepository.delete(attributeValue.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Giá trị thuộc tính đã xóa")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAttributeValues(int attributeId) {
        Optional<Attribute> attribute = attributeRepository.findById(attributeId);
        if (attribute.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Thuộc tính: " + attributeId + " không tồn tại")
                            .build());
        }

        List<AttributeValue> attributeValues = attribute.get().getAttributeValues();

        List<AttributeValueResponse> attributeValueResponses = attributeValues.stream()
                .map(attributeValue -> AttributeValueResponse.builder()
                        .attributeValueId(attributeId)
                        .value(attributeValue.getValue())
                        .hexCode(attributeValue.getHexCode())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Get all values belong to attribute: " + attribute.get().getName())
                        .data(attributeValueResponses)
                        .build());
    }


}
