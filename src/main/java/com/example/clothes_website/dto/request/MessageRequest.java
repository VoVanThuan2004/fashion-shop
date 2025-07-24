package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MessageRequest {
    private int conversationId;
    private String textMessage;
    private String messageType;
}
