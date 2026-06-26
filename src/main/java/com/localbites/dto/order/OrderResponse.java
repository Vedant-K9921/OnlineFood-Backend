package com.localbites.dto.order;

import com.localbites.enums.OrderStatus;
import com.localbites.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    private Long userId;

    private Long restaurantId;

    private String deliveryAddress;

    private OrderStatus status;

    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}