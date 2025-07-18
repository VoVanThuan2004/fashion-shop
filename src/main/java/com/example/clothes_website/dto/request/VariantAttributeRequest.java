package com.example.clothes_website.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VariantAttributeRequest {
    private int attributeId;
    private int productVariantId;
    private String value;
}
