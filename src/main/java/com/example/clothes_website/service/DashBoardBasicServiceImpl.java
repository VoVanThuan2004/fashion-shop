package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.DashBoardBasicResponse;
import com.example.clothes_website.repository.OrderRepository;
import com.example.clothes_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashBoardBasicServiceImpl implements DashBoardBasicService {
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
}
