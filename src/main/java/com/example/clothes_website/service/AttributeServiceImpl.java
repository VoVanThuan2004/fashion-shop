package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AttributeRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.AttributeResponse;
import com.example.clothes_website.entity.Attribute;
import com.example.clothes_website.entity.UserRole;
import com.example.clothes_website.repository.AttributeRepository;
import com.example.clothes_website.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {
    private final AttributeRepository attributeRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> addAttribute(AttributeRequest attributeRequest) {
        String code = toSlug(attributeRequest.getName());

        // Check code có ton tại kh
        Optional<Attribute> attribute = attributeRepository.findByCode(code);
        if (attribute.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Thuộc tính " + attributeRequest.getName() + " đã tồn tại")
                            .build());
        }

        Attribute at = new Attribute();
        at.setName(attributeRequest.getName());
        at.setCode(code);
        at.setType(attributeRequest.getType());
        at.setPriority(attributeRequest.getPriority());
        attributeRepository.save(at);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Thuộc tính đã tạo thành công")
                        .build());
    }

    @Override
    public List<AttributeResponse> getAllAttributes() {
        List<Attribute> attributes = attributeRepository.findAll();

        return attributes.stream()
                .map(attribute -> AttributeResponse.builder()
                        .attributeId(attribute.getAttributeId())
                        .name(attribute.getName())
                        .code(attribute.getCode())
                        .type(attribute.getType())
                        .priority(attribute.getPriority())
                        .build())
                .toList();
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateAttribute(int attributeId, AttributeRequest attributeRequest) {
        Optional<Attribute> attribute = attributeRepository.findById(attributeId);
        if (attribute.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Thuộc tính: " + attributeId + " không tồn tại")
                            .build());
        }

        // Nếu name request khác với ban đầu thì check mã code mới
        if (!attribute.get().getName().equals(attributeRequest.getName())) {
            String code = toSlug(attributeRequest.getName());
            Optional<Attribute> at = attributeRepository.findByCode(code);
            if (at.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message("Tên thuộc tính: " + attributeRequest.getName() + " đã tồn tại")
                                .build());
            }

            attribute.get().setName(attributeRequest.getName());
            attribute.get().setCode(code);
        }

        else {
            attribute.get().setName(attributeRequest.getName());
            attribute.get().setType(attributeRequest.getType());
            attribute.get().setPriority(attributeRequest.getPriority());
        }

        attributeRepository.save(attribute.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Thuộc tính đã cập nhật thành công")
                        .build());
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
