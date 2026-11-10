package com.example.clothes_website.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DashboardStatisticResponse {
    private int period;
    private int totalOrders;
    private double totalRevenue;
    private double totalProfit;
}
