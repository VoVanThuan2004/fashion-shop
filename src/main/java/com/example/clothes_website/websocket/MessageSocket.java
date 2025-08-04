package com.example.clothes_website.websocket;

import com.example.clothes_website.dto.response.ConversationResponse;
import com.example.clothes_website.dto.response.MessageResponse;
import com.example.clothes_website.entity.ImageMessages;
import com.example.clothes_website.entity.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSocket {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastMessage(Messages message) {
        MessageResponse messageResponse = MessageResponse.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getFullName())
                .sendTime(message.getSendTime())
                .textMessage(message.getTextMessage())
                .messageType(message.getMessageType())
                .conversationId(message.getConversation().getConversationId())
                .files(message.getImageMessages().stream()
                        .map(ImageMessages::getImagePath)
                        .toList()
                )
                .build();

        // Cập nhật lại danh sách cuộc hội thoại cho người nhận - khi có tin nhắn mới
        ConversationResponse conversationResponse = ConversationResponse.builder()
                .conversationId(message.getConversation().getConversationId())
                .customerId(message.getConversation().getCustomer().getUserId())
                .customerName(message.getConversation().getCustomer().getFullName())
                .avatar(message.getConversation().getCustomer().getImage())
                .lastMessage(message.getTextMessage() != null
                                ? message.getTextMessage()
                                : "Đã gửi tệp đính kèm"
                        )
                .lastMessageTime(message.getConversation().getLastMessageTime())
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/messages/" + message.getConversation().getConversationId(), messageResponse);
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationResponse.getCustomerId(), conversationResponse);
    }
}
