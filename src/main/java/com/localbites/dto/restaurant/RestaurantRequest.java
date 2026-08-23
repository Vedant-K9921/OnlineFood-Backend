package com.localbites.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 150, message = "Restaurant name must not exceed 150 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    @Size(max = 1000, message = "Image URL must not exceed 1000 characters")
    private String imageUrl;

    @Size(max = 100, message = "Cuisine type must not exceed 100 characters")
    private String cuisineType;

    private Boolean isOpen;
}
