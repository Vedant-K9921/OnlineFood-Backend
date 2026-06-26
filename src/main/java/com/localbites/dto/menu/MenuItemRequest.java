package com.localbites.dto.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequest {

    @NotNull
    private Long restaurantId;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    private String imageUrl;

    private String category;

    private Boolean isVeg;

    private Boolean isAvailable;
}