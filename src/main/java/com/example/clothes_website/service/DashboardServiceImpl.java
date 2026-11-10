package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.DashBoardBasicResponse;
import com.example.clothes_website.dto.response.DashboardStatisticResponse;
import com.example.clothes_website.repository.OrderRepository;
import com.example.clothes_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public ResponseEntity<ApiResponse<DashBoardBasicResponse>> getDashBoardBasic() {
        try {
            // Tạo thời gian lấy người dùng mới (kể từ 1 tuần)
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusDays(7);

            Integer totalUsers = userRepository.getTotalUsers();
            Integer totalNewUsers = userRepository.getTotalNewUsers(startTime, endTime);
            Integer totalOrders = orderRepository.getTotalOrders();
            Double totalRevenue = orderRepository.getTotalRevenue();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.<DashBoardBasicResponse>builder()
                            .status("success")
                            .code(HttpStatus.OK.value())
                            .message("DashBoard cơ bản")
                            .data(new DashBoardBasicResponse(totalUsers, totalNewUsers, totalOrders, totalRevenue))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DashBoardBasicResponse>builder()
                            .status("error")
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardStatisticResponse>>> getDashBoardStatisticsByTime(String type) {
        // Lấy ra năm hiện tại
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        int year = now.getYear();

        List<Object[]> results = new ArrayList<>();
        List<DashboardStatisticResponse> dashboardStatisticResponses = new ArrayList<>();

        try {
            switch (type.toUpperCase()) {
                case "MONTH" -> {
                    results = orderRepository.getMonthlyStatistics(year);
                }
                case "QUARTER" -> {
                    results = orderRepository.getQuarterlyStatistics(year);
                }
                case "WEEK" -> {
                    LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
                    LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay();
                    LocalDateTime endOfWeek = startOfWeek.plusDays(7);
                    results = orderRepository.getWeeklyStatistics(startOfWeek, endOfWeek);
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.<List<DashboardStatisticResponse>>builder()
                                    .status("error")
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Loại thời gian thống kê không hợp lệ")
                                    .build());
                }
            }

            // Chuyển dữ liệu Object -> Response
            dashboardStatisticResponses = mapToDashboardStatisticResponse(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DashboardStatisticResponse>>builder()
                            .status("error")
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi hệ thống: " + e.getMessage())
                            .build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<List<DashboardStatisticResponse>>builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Thống kê theo " + type.toUpperCase())
                        .data(dashboardStatisticResponses)
                        .build());

    }

    private List<DashboardStatisticResponse> mapToDashboardStatisticResponse(List<Object[]> rows) {
        List<DashboardStatisticResponse> dashboardStatisticResponses = new ArrayList<>();
        for (Object[] row : rows) {
            dashboardStatisticResponses.add(new DashboardStatisticResponse(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue(),
                    ((Number) row[2]).doubleValue(),
                    ((Number) row[3]).doubleValue()
            ));
        }

        return dashboardStatisticResponses;
    }
}
