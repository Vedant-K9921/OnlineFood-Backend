package com.localbites.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderResponse {

    private Long id;
    private Long userId;
    private Long restaurantId;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;
}