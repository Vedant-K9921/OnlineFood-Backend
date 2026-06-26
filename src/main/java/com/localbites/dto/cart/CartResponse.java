package com.localbites.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private List<CartItemResponse> items;

    private BigDecimal totalAmount;
}