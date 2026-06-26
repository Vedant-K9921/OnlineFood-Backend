package com.localbites.dto.owner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDashboardResponse {

    private Long restaurantId;

    private String restaurantName;

    private Long totalOrders;

    private Long deliveredOrders;

    private Long pendingOrders;

    private BigDecimal totalRevenue;
}