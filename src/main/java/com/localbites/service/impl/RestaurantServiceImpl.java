package com.localbites.service.impl;

import com.localbites.dto.restaurant.RestaurantRequest;
import com.localbites.dto.restaurant.RestaurantResponse;
import com.localbites.entity.Restaurant;
import com.localbites.entity.User;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.repository.RestaurantRepository;
import com.localbites.repository.UserRepository;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl
        implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Override
    public RestaurantResponse createRestaurant(
            RestaurantRequest request
    ) {

        User owner = getCurrentUser();

        Restaurant restaurant =
                Restaurant.builder()
                        .owner(owner)
                        .name(request.getName())
                        .description(request.getDescription())
                        .address(request.getAddress())
                        .phone(request.getPhone())
                        .imageUrl(request.getImageUrl())
                        .cuisineType(request.getCuisineType())
                        .isOpen(
                                request.getIsOpen() == null
                                        ? true
                                        : request.getIsOpen()
                        )
                        .rating(0.0)
                        .build();

        Restaurant saved =
                restaurantRepository.save(restaurant);

        return mapToResponse(saved);
    }

    @Override
    public RestaurantResponse updateRestaurant(
            Long restaurantId,
            RestaurantRequest request
    ) {

        Restaurant restaurant =
                restaurantRepository.findById(restaurantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Restaurant not found"
                                ));

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setCuisineType(request.getCuisineType());

        if (request.getIsOpen() != null) {
            restaurant.setIsOpen(
                    request.getIsOpen()
            );
        }

        Restaurant updated =
                restaurantRepository.save(restaurant);

        return mapToResponse(updated);
    }

    @Override
    public void deleteRestaurant(
            Long restaurantId
    ) {

        Restaurant restaurant =
                restaurantRepository.findById(restaurantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Restaurant not found"
                                ));

        restaurantRepository.delete(restaurant);
    }

    @Override
    public RestaurantResponse getRestaurantById(
            Long restaurantId
    ) {

        Restaurant restaurant =
                restaurantRepository.findById(restaurantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Restaurant not found"
                                ));

        return mapToResponse(restaurant);
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {

        return restaurantRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
public List<RestaurantResponse> getOwnerRestaurants() {

    User owner = getCurrentUser();

    Restaurant restaurant =
            restaurantRepository
                    .findByOwnerId(owner.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Restaurant not found"
                            ));

    return List.of(
            mapToResponse(restaurant)
    );
}

    private RestaurantResponse mapToResponse(
            Restaurant restaurant
    ) {

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .ownerId(
                        restaurant.getOwner().getId()
                )
                .name(restaurant.getName())
                .description(
                        restaurant.getDescription()
                )
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .imageUrl(
                        restaurant.getImageUrl()
                )
                .cuisineType(
                        restaurant.getCuisineType()
                )
                .isOpen(
                        restaurant.getIsOpen()
                )
                .rating(
                        restaurant.getRating()
                )
                .build();
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        return userRepository.findById(
                        userDetails.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }
}