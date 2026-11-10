package com.example.clothes_website.controller;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.DashBoardBasicResponse;
import com.example.clothes_website.dto.response.DashboardStatisticResponse;
import com.example.clothes_website.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("basic/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashBoardBasicResponse>> getDashBoardBasic() {
        return dashboardService.getDashBoardBasic();
    }

    @GetMapping("advanced/statistics/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DashboardStatisticResponse>>> getDashBoardStatisticsByTime(
            @RequestParam String type
    ) {
        return dashboardService.getDashBoardStatisticsByTime(type);
    }
}
