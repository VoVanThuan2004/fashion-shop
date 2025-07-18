package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AttributeRequest {
    private String name;
    private String type;
    private int priority;
}
