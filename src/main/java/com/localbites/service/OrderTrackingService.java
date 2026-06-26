package com.localbites.service;

import com.localbites.enums.OrderStatus;

public interface OrderTrackingService {

    void broadcastOrderStatus(
            Long orderId,
            OrderStatus status
    );
}