package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.MessageRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // Tạo cuộc hội thoại giữa 2 người
    @PostMapping("/api/v1/conversation/")
    public ResponseEntity<ApiResponse<?>> createConversation() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        int customerId = userDetails.getId();
        return messageService.createConversation(customerId);
    }

    @PostMapping("/api/v1/message/")
    public ResponseEntity<ApiResponse<?>> sendMessage(
            @RequestPart MessageRequest messageRequest,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return messageService.sendMessage(userId, messageRequest, files);
    }
}
