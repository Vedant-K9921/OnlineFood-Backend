package com.localbites.service;

import com.localbites.dto.order.OrderResponse;
import com.localbites.dto.order.PlaceOrderRequest;
import com.localbites.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(
            Long userId,
            PlaceOrderRequest request
    );

    List<OrderResponse> getMyOrders(
            Long userId
    );

    OrderResponse getOrderById(
            Long orderId,
            Long userId
    );

    List<OrderResponse> getRestaurantOrders(
            Long restaurantId
    );

    OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus status
    );
}