package com.example.clothes_website.websocket;

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

        messagingTemplate.convertAndSend("/topic/messages/" + message.getConversation().getConversationId(), messageResponse);
    }
}
