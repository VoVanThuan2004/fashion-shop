package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ApiResponse<T> {
    private String status;
    private int code;
    private String message;
    private T data;
}
