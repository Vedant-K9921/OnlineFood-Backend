package com.localbites.dto.menu;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {

    private Long id;

    private Long restaurantId;

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private String category;

    private Boolean isVeg;

    private Boolean isAvailable;
}