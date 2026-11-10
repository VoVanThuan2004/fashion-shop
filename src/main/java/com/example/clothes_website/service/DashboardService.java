package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.DashBoardBasicResponse;
import com.example.clothes_website.dto.response.DashboardStatisticResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DashboardService {
    ResponseEntity<ApiResponse<DashBoardBasicResponse>> getDashBoardBasic();

    ResponseEntity<ApiResponse<List<DashboardStatisticResponse>>> getDashBoardStatisticsByTime(String type);
}
