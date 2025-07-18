package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttributeValueUpdate {
    private String value;
    private String hexCode;
}
