package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.cloudinary.UploadResult;
import com.example.clothes_website.dto.request.MessageRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ConversationResponse;
import com.example.clothes_website.dto.response.MessageResponse;
import com.example.clothes_website.entity.*;
import com.example.clothes_website.repository.*;
import com.example.clothes_website.websocket.MessageSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageMessageRepository imageMessageRepository;
    private final MessageSocket messageSocket;

    @Override
    public ResponseEntity<ApiResponse<?>> sendMessage(int userId, MessageRequest messageRequest, List<MultipartFile> files) {
        // Kiểm tra user
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Người dùng không tồn tại - ID: " + userId)
                            .build());
        }

        // Kiểm tra conversation
        Optional<Conversations> conversation = conversationRepository.findById(messageRequest.getConversationId());
        if (conversation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Cuộc hội thoại không tồn tại")
                            .build());
        }

        // Kiểm tra có gửi ảnh hay không
        List<ImageMessages> imageMessages = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    UploadResult uploadResult = cloudinaryService.uploadFile(file);
                    ImageMessages imageMessage = new ImageMessages();
                    imageMessage.setImagePath(uploadResult.getUrl());
                    imageMessage.setPublicId(uploadResult.getPublicId());
                    imageMessage.setFileName(file.getOriginalFilename());
                    imageMessage.setFileType(file.getContentType());
                    imageMessages.add(imageMessage);

                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.builder()
                                    .status("error")
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Lỗi khi gửi file: " + e.getMessage())
                                    .build());
                }
            }
        }

        if (imageMessages != null && !imageMessages.isEmpty()) {
            imageMessageRepository.saveAll(imageMessages);
        }

        // Người gửi -> userId
        Messages messages = new Messages();
        messages.setTextMessage(messageRequest.getTextMessage());
        messages.setMessageType(messageRequest.getMessageType());
        messages.setConversation(conversation.get());
        messages.setSender(user.get());
        messages.setSendTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        messages.setImageMessages(imageMessages);
        messages.setRead(false);
        messages.getConversation().setLastMessageTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        messages = messageRepository.save(messages);

        // Gửi lên websocket
        messageSocket.broadcastMessage(messages);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Tin nhắn đã được gửi thành công - sender: " + messages.getSender().getFullName())
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> createConversation(int customerId) {
        Optional<Users> user = userRepository.findById(customerId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NO_CONTENT.value())
                            .message("Người dùng không tồn tại")
                            .build());
        }

        // Kiểm tra cuộc hội đó tồn tại hay chưa
        Optional<Conversations> conversation = conversationRepository.findConversationsByCustomerId(customerId);
        if (conversation.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Cuộc hội thoại của người dùng đã tồn tại")
                            .build());
        }

        // Tìm tài khoản admin
        Optional<Users> admin = userRepository.findAdmin();
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Admin không tồn tại")
                            .build());
        }

        // Tạo mới cuộc hội thoại
        Conversations conversations = new Conversations();
        conversations.setCustomer(user.get());
        conversations.setAdmin(admin.get());
        conversations.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        conversationRepository.save(conversations);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Cuộc hội thoại đã được tạo")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllMessagesInConversation(int conversationId, int page, int size) {
        // Kiểm tra hội thoại
        Optional<Conversations> conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Cuộc hội thoại không tồn tại")
                            .build());
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("sendTime").descending()
        );

        Page<Messages> messages = messageRepository.getAllMessageInConversation(conversationId, pageable);

        // Chuyển về response
        Page<MessageResponse> messageResponses = messages.map(
                m -> MessageResponse.builder()
                        .messageId(m.getMessageId())
                        .textMessage(m.getTextMessage())
                        .messageType(m.getMessageType())
                        .senderId(m.getSender().getUserId())
                        .senderName(m.getSender().getFullName())
                        .sendTime(m.getSendTime())
                        .files(m.getImageMessages().stream()
                                .map(ImageMessages::getImagePath)
                                .toList()
                        )
                        .isRead(m.isRead())
                        .conversationId(m.getConversation().getConversationId())
                        .build()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy tất cả tin nhắn trong cuộc hội thoại")
                        .data(messageResponses)
                        .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> markAsRead(int conversationId, int userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NO_CONTENT.value())
                            .message("Người dùng không tồn tại")
                            .build());
        }

        // Kiểm tra hội thoại
        Optional<Conversations> conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Cuộc hội thoại không tồn tại")
                            .build());
        }


        int updatedCount = messageRepository.markMessagesAsReadInConversation(conversationId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message(updatedCount + " tin nhắn đã được đánh dấu là đã đọc")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllConversations(int adminId, int page, int size) {
        Optional<Users> user = userRepository.findById(adminId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NO_CONTENT.value())
                            .message("Người dùng không tồn tại")
                            .build());
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("lastMessageTime").descending()
        );

        Page<Conversations> conversations = conversationRepository.findAllConversation(pageable);

        Page<ConversationResponse> conversationResponses = conversations.map(
                conversation -> ConversationResponse.builder()
                        .conversationId(conversation.getConversationId())
                        .customerId(conversation.getCustomer().getUserId())
                        .customerName(conversation.getCustomer().getFullName())
                        .avatar(conversation.getCustomer().getImage())
                        .lastMessage(conversation.getMessages()
                                .stream()
                                .max(Comparator.comparing(Messages::getSendTime))
                                .get().getTextMessage()
                        )
                        .lastMessageTime(conversation.getLastMessageTime())
                        .isRead(conversation.getMessages().stream()
                                .anyMatch(m -> !m.isRead() && m.getSender().getUserId() != adminId)
                        )
                        .build()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy tất cả các cuộc hội")
                        .data(conversationResponses)
                        .build());
    }
}
