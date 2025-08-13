package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.DashBoardBasicResponse;
import com.example.clothes_website.service.DashBoardBasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard/basic")
@RequiredArgsConstructor
public class DashBoardBasicController {
    private final DashBoardBasicService dashBoardBasicService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashBoardBasicResponse>> getDashBoardBasic() {
        return dashBoardBasicService.getDashBoardBasic();
    }
}
