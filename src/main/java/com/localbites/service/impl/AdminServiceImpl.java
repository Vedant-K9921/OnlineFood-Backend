package com.localbites.service.impl;

import com.localbites.dto.admin.AdminDashboardResponse;
import com.localbites.repository.OrderRepository;
import com.localbites.repository.RestaurantRepository;
import com.localbites.repository.UserRepository;
import com.localbites.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl
        implements AdminService {

    private final UserRepository userRepository;

    private final RestaurantRepository restaurantRepository;

    private final OrderRepository orderRepository;

    @Override
    public AdminDashboardResponse getDashboard() {

        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalRestaurants(
                        restaurantRepository.count()
                )
                .totalOrders(
                        orderRepository.count()
                )
                .totalRevenue(
                        orderRepository.getPlatformRevenue()
                )
                .build();
    }
}