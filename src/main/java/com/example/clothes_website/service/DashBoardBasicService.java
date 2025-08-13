package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.DashBoardBasicResponse;
import org.springframework.http.ResponseEntity;

public interface DashBoardBasicService {
    ResponseEntity<ApiResponse<DashBoardBasicResponse>> getDashBoardBasic();
}
