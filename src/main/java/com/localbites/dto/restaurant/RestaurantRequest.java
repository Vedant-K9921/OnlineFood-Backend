package com.localbites.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequest {

    @NotBlank
    private String name;

    private String description;

    private String address;

    private String phone;

    private String imageUrl;

    private String cuisineType;

    private Boolean isOpen;
}