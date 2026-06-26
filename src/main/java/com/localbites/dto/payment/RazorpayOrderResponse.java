package com.localbites.dto.payment;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {

    private String razorpayOrderId;

    private String key;

    private Long amount;

    private String currency;

    private Long orderId;
}