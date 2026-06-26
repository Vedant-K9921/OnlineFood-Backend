package com.localbites.dto.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long menuItemId;

    private String menuItemName;

    private Integer quantity;

    private BigDecimal priceAtOrder;

    private BigDecimal subtotal;
}