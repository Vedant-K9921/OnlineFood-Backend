package com.localbites.service.impl;

import com.localbites.dto.websocket.OrderStatusUpdateDto;
import com.localbites.enums.OrderStatus;
import com.localbites.service.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTrackingServiceImpl
        implements OrderTrackingService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcastOrderStatus(
            Long orderId,
            OrderStatus status
    ) {

        OrderStatusUpdateDto payload =
                OrderStatusUpdateDto.builder()
                        .orderId(orderId)
                        .status(status)
                        .message(
                                "Order status updated to "
                                        + status.name()
                        )
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/orders/" + orderId,
                payload
        );
    }
}