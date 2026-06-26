package com.localbites.controller;

import com.localbites.dto.admin.AdminDashboardResponse;
import com.localbites.dto.admin.AdminRestaurantResponse;
import com.localbites.dto.admin.AdminOrderResponse;
import com.localbites.entity.User;
import com.localbites.response.ApiResponse;
import com.localbites.repository.UserRepository;
import com.localbites.repository.RestaurantRepository;
import com.localbites.repository.OrderRepository;
import com.localbites.enums.OrderStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    // ---------------- DASHBOARD ----------------
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> dashboard() {

        AdminDashboardResponse res = AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalRestaurants(restaurantRepository.count())
                .totalOrders(orderRepository.count())
                .totalRevenue(orderRepository.getPlatformRevenue())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<AdminDashboardResponse>builder()
                        .success(true)
                        .message("Dashboard fetched")
                        .data(res)
                        .build()
        );
    }

    // ---------------- USERS ----------------
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {

        return ResponseEntity.ok(
                ApiResponse.<List<User>>builder()
                        .success(true)
                        .message("Users fetched")
                        .data(userRepository.findAll())
                        .build()
        );
    }

    // ---------------- RESTAURANTS ----------------
    @GetMapping("/restaurants")
public ResponseEntity<ApiResponse<List<AdminRestaurantResponse>>> getRestaurants() {

    List<AdminRestaurantResponse> list =
            restaurantRepository.findAll()
                    .stream()
                    .map(r -> AdminRestaurantResponse.builder()
                            .id(r.getId())
                            .name(r.getName())
                            .address(r.getAddress())
                            .phone(r.getPhone())
                            .isOpen(r.getIsOpen())
                            .build()
                    )
                    .toList();

    return ResponseEntity.ok(
            ApiResponse.<List<AdminRestaurantResponse>>builder()
                    .success(true)
                    .message("Restaurants fetched")
                    .data(list)
                    .build()
    );
}

    // ---------------- ORDERS ----------------
    @GetMapping("/orders")
public ResponseEntity<ApiResponse<List<AdminOrderResponse>>> getOrders() {

    List<AdminOrderResponse> list =
            orderRepository.findAll()
                    .stream()
                    .map(o -> AdminOrderResponse.builder()
                            .id(o.getId())
                            .userId(o.getUser().getId())
                            .restaurantId(o.getRestaurant().getId())
                            .totalAmount(o.getTotalAmount())
                            .status(o.getStatus().name())
                            .paymentStatus(o.getPaymentStatus().name())
                            .createdAt(o.getCreatedAt())
                            .build()
                    )
                    .toList();

    return ResponseEntity.ok(
            ApiResponse.<List<AdminOrderResponse>>builder()
                    .success(true)
                    .message("Orders fetched")
                    .data(list)
                    .build()
    );
}
}