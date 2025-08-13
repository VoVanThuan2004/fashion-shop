package com.example.clothes_website.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DashBoardBasicResponse {
    private Integer totalUsers;
    private Integer totalNewUsers;
    private Integer totalOrders;
    private Double totalRevenue;
}
