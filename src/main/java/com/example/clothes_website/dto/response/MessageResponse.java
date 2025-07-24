package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class MessageResponse {
    private int messageId;
    private int senderId;
    private String senderName;
    private String textMessage;
    private String messageType;
    private LocalDateTime sendTime;
    private List<String> files;
    private int conversationId;
}
