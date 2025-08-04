package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class ConversationResponse {
    private int conversationId;
    private int customerId;
    private String customerName;
    private String avatar;

    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean isRead;
}
