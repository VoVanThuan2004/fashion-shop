package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AttributeValueResponse {
    private int attributeValueId;
    private String value;
    private String hexCode;
}
