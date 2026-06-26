package com.localbites.controller;

import com.localbites.dto.owner.OwnerDashboardResponse;
import com.localbites.response.ApiResponse;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.localbites.dto.order.OrderResponse;
import com.localbites.enums.OrderStatus;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OwnerDashboardResponse>>
    getDashboard(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {

        OwnerDashboardResponse dashboard =
                ownerService.getDashboard(
                        userDetails.getUserId()
                );

        return ResponseEntity.ok(
                ApiResponse.<OwnerDashboardResponse>builder()
                        .success(true)
                        .message("Dashboard fetched successfully")
                        .data(dashboard)
                        .build()
        );
    }

        @GetMapping("/orders")
        @PreAuthorize("hasRole('OWNER')")
        public ResponseEntity<ApiResponse<List<OrderResponse>>>
        getOrders(
        @AuthenticationPrincipal
        CustomUserDetails userDetails
        ) {

    List<OrderResponse> orders =
            ownerService.getRestaurantOrders(
                    userDetails.getUserId()
            );

    return ResponseEntity.ok(
            ApiResponse.<List<OrderResponse>>builder()
                    .success(true)
                    .message("Orders fetched successfully")
                    .data(orders)
                    .build()
    );
        }

        @PutMapping("/orders/{orderId}/status")
        @PreAuthorize("hasRole('OWNER')")
        public ResponseEntity<ApiResponse<OrderResponse>>
        updateStatus(
        @PathVariable Long orderId,
        @RequestParam OrderStatus status,
        @AuthenticationPrincipal
        CustomUserDetails userDetails
        ) {

    OrderResponse response =
            ownerService.updateOrderStatus(
                    userDetails.getUserId(),
                    orderId,
                    status
            );

    return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
                    .success(true)
                    .message("Order status updated")
                    .data(response)
                    .build()
    );
        }

        
}