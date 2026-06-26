package com.localbites.service;

import com.localbites.dto.owner.OwnerDashboardResponse;
import com.localbites.dto.order.OrderResponse;
import com.localbites.enums.OrderStatus;

import java.util.List;

public interface OwnerService {

    OwnerDashboardResponse getDashboard(
            Long ownerId
    );

    List<OrderResponse> getRestaurantOrders(
        Long ownerId
    );

    OrderResponse updateOrderStatus(
        Long ownerId,
        Long orderId,
        OrderStatus status
    );
}