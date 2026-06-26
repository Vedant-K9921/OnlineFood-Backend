package com.localbites.controller;

import com.localbites.dto.restaurant.RestaurantRequest;
import com.localbites.dto.restaurant.RestaurantResponse;
import com.localbites.response.ApiResponse;
import com.localbites.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>>
    createRestaurant(
            @Valid @RequestBody RestaurantRequest request
    ) {

        RestaurantResponse response =
                restaurantService.createRestaurant(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<RestaurantResponse>builder()
                        .success(true)
                        .message(
                                "Restaurant created successfully"
                        )
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>>
    updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request
    ) {

        RestaurantResponse response =
                restaurantService.updateRestaurant(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<RestaurantResponse>builder()
                        .success(true)
                        .message(
                                "Restaurant updated successfully"
                        )
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<String>>
    deleteRestaurant(
            @PathVariable Long id
    ) {

        restaurantService.deleteRestaurant(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(
                                "Restaurant deleted successfully"
                        )
                        .data("Deleted")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>>
    getRestaurantById(
            @PathVariable Long id
    ) {

        RestaurantResponse response =
                restaurantService.getRestaurantById(id);

        return ResponseEntity.ok(
                ApiResponse.<RestaurantResponse>builder()
                        .success(true)
                        .message("Restaurant fetched")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>>
    getAllRestaurants() {

        List<RestaurantResponse> response =
                restaurantService.getAllRestaurants();

        return ResponseEntity.ok(
                ApiResponse.<List<RestaurantResponse>>builder()
                        .success(true)
                        .message(
                                "Restaurants fetched successfully"
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>>
    getOwnerRestaurants() {

        List<RestaurantResponse> response =
                restaurantService.getOwnerRestaurants();

        return ResponseEntity.ok(
                ApiResponse.<List<RestaurantResponse>>builder()
                        .success(true)
                        .message(
                                "Owner restaurants fetched"
                        )
                        .data(response)
                        .build()
        );
    }
}