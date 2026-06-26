package com.localbites.dto.restaurant;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {

    private Long id;

    private Long ownerId;

    private String name;

    private String description;

    private String address;

    private String phone;

    private String imageUrl;

    private String cuisineType;

    private Boolean isOpen;

    private Double rating;
}