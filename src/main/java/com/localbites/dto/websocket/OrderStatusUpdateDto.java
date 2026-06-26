package com.localbites.dto.websocket;

import com.localbites.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {

    private Long orderId;

    private OrderStatus status;

    private String message;
}