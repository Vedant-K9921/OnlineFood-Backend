package com.localbites.dto.cart;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private Long menuItemId;

    private String menuItemName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}