package com.example.clothes_website.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProductSearchRequest extends ProductSearchRequest {
    private Boolean isActive;
}
