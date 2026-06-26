package com.localbites.dto.admin;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private Long totalUsers;

    private Long totalRestaurants;

    private Long totalOrders;

    private BigDecimal totalRevenue;
}