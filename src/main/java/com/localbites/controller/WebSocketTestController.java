package com.localbites.controller;

import com.localbites.dto.websocket.OrderStatusUpdateDto;
import com.localbites.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ws-test")
@RequiredArgsConstructor
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/{orderId}")
    public String sendTestMessage(
            @PathVariable Long orderId
    ) {

        OrderStatusUpdateDto dto =
                OrderStatusUpdateDto.builder()
                        .orderId(orderId)
                        .status(OrderStatus.PREPARING)
                        .message("Test WebSocket Message")
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/orders/" + orderId,
                dto
        );

        return "Message Sent";
    }
}