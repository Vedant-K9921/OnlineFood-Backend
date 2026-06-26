package com.localbites.controller;

import com.localbites.dto.menu.MenuItemRequest;
import com.localbites.dto.menu.MenuItemResponse;
import com.localbites.response.ApiResponse;
import com.localbites.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>>
    createMenuItem(
            @Valid @RequestBody MenuItemRequest request
    ) {

        MenuItemResponse response =
                menuItemService.createMenuItem(request);

        return ResponseEntity.ok(
                ApiResponse.<MenuItemResponse>builder()
                        .success(true)
                        .message("Menu item created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>>
    updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request
    ) {

        MenuItemResponse response =
                menuItemService.updateMenuItem(id, request);

        return ResponseEntity.ok(
                ApiResponse.<MenuItemResponse>builder()
                        .success(true)
                        .message("Menu item updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<String>>
    deleteMenuItem(
            @PathVariable Long id
    ) {

        menuItemService.deleteMenuItem(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Menu item deleted successfully")
                        .data("Deleted")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>>
    getMenuItem(
            @PathVariable Long id
    ) {

        MenuItemResponse response =
                menuItemService.getMenuItemById(id);

        return ResponseEntity.ok(
                ApiResponse.<MenuItemResponse>builder()
                        .success(true)
                        .message("Menu item fetched")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>>
    getRestaurantMenu(
            @PathVariable Long restaurantId
    ) {

        List<MenuItemResponse> response =
                menuItemService.getMenuByRestaurant(
                        restaurantId
                );

        return ResponseEntity.ok(
                ApiResponse.<List<MenuItemResponse>>builder()
                        .success(true)
                        .message("Menu fetched")
                        .data(response)
                        .build()
        );
    }
}