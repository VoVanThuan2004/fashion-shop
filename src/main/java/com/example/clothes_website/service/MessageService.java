package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.MessageRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    ResponseEntity<ApiResponse<?>> sendMessage(int userId, MessageRequest messageRequest, List<MultipartFile> files);

    ResponseEntity<ApiResponse<?>> createConversation(int userId);

    ResponseEntity<ApiResponse<?>> getAllMessagesInConversation(int conversationId, int page, int size);

    ResponseEntity<ApiResponse<?>> markAsRead(int conversationId, int userId);

    ResponseEntity<ApiResponse<?>> getAllConversations(int userId, int page, int size);
}
