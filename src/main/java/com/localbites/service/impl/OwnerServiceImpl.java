package com.localbites.service.impl;

import com.localbites.dto.order.OrderResponse;
import com.localbites.dto.owner.OwnerDashboardResponse;
import com.localbites.entity.Order;
import com.localbites.entity.Restaurant;
import com.localbites.enums.OrderStatus;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.exception.UnauthorizedException;
import com.localbites.repository.OrderRepository;
import com.localbites.repository.RestaurantRepository;
import com.localbites.service.OrderService;
import com.localbites.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    public OwnerDashboardResponse getDashboard(Long ownerId) {
        Restaurant restaurant = getOwnedRestaurant(ownerId);

        Long totalOrders = orderRepository.countByRestaurantId(restaurant.getId());
        Long deliveredOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.DELIVERED);
        Long pendingOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.PLACED);
        BigDecimal revenue = orderRepository.getTotalRevenue(restaurant.getId());

        return OwnerDashboardResponse.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .totalOrders(totalOrders)
                .deliveredOrders(deliveredOrders)
                .pendingOrders(pendingOrders)
                .totalRevenue(revenue)
                .build();
    }

    @Override
    public List<OrderResponse> getRestaurantOrders(Long ownerId) {
        return orderService.getRestaurantOrders(getOwnedRestaurant(ownerId).getId());
    }

    @Override
    public OrderResponse updateOrderStatus(Long ownerId, Long orderId, OrderStatus status) {
        Restaurant restaurant = getOwnedRestaurant(ownerId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!restaurant.getId().equals(order.getRestaurant().getId())) {
            throw new UnauthorizedException("You cannot manage this order");
        }

        return orderService.updateOrderStatus(orderId, status);
    }

    private Restaurant getOwnedRestaurant(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }
}
