package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.MessageRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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

    // Nhắn tin
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

    // Lấy tất cả tin nhắn trong 1 cuộc hội thoại
    @GetMapping("/api/v1/message/{conversationId}")
    public ResponseEntity<ApiResponse<?>> getAllMessagesInConversation(
            @PathVariable int conversationId,
            @RequestParam int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return messageService.getAllMessagesInConversation(conversationId, page, size);
    }

    // Đánh dấu tin nhắn đã đọc
    @PutMapping("/api/v1/conversation/{conversationId}/read")
    public ResponseEntity<ApiResponse<?>> markAsRead(@PathVariable int conversationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return messageService.markAsRead(conversationId, userId);
    }

    // Lấy tất cả cuộc hội thoại
    @GetMapping("/api/v1/conversation/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllConversations(
            @RequestParam int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return messageService.getAllConversations(userId, page, size);
    }
}
