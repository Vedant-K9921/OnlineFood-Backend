package com.localbites.controller;

import com.localbites.dto.order.OrderResponse;
import com.localbites.dto.order.PlaceOrderRequest;
import com.localbites.dto.order.UpdateOrderStatusRequest;
import com.localbites.entity.User;
import com.localbites.response.ApiResponse;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<OrderResponse> placeOrder(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,
            @Valid
            @RequestBody
            PlaceOrderRequest request
    ) {

        return ApiResponse.success(
                "Order placed successfully",
                orderService.placeOrder(
                        userDetails.getUserId(),
                        request
                )
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<OrderResponse>> myOrders(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {

        return ApiResponse.success(
                "Orders fetched successfully",
                orderService.getMyOrders(
                        userDetails.getUserId()
                )
        );
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<OrderResponse> getOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {

        return ApiResponse.success(
                "Order fetched successfully",
                orderService.getOrderById(
                        orderId,
                        userDetails.getUserId()
                )
        );
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<List<OrderResponse>>
    restaurantOrders(
            @PathVariable Long restaurantId
    ) {

        return ApiResponse.success(
                "Restaurant orders fetched",
                orderService.getRestaurantOrders(
                        restaurantId
                )
        );
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<OrderResponse>
    updateStatus(
            @PathVariable Long orderId,
            @Valid
            @RequestBody
            UpdateOrderStatusRequest request
    ) {

        return ApiResponse.success(
                "Order status updated",
                orderService.updateOrderStatus(
                        orderId,
                        request.getStatus()
                )
        );
    }
}