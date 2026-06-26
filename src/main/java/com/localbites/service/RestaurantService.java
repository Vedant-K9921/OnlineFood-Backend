package com.localbites.service;

import com.localbites.dto.restaurant.RestaurantRequest;
import com.localbites.dto.restaurant.RestaurantResponse;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse createRestaurant(
            RestaurantRequest request
    );

    RestaurantResponse updateRestaurant(
            Long restaurantId,
            RestaurantRequest request
    );

    void deleteRestaurant(Long restaurantId);

    RestaurantResponse getRestaurantById(
            Long restaurantId
    );

    List<RestaurantResponse> getAllRestaurants();

    List<RestaurantResponse> getOwnerRestaurants();
}