package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AttributeResponse {
    private int attributeId;

    private String name;
    private String code;
    private String type;
    private int priority;
}
