package com.localbites.service;

import com.localbites.dto.menu.MenuItemRequest;
import com.localbites.dto.menu.MenuItemResponse;

import java.util.List;

public interface MenuItemService {

    MenuItemResponse createMenuItem(
            MenuItemRequest request
    );

    MenuItemResponse updateMenuItem(
            Long menuItemId,
            MenuItemRequest request
    );

    void deleteMenuItem(
            Long menuItemId
    );

    MenuItemResponse getMenuItemById(
            Long menuItemId
    );

    List<MenuItemResponse> getMenuByRestaurant(
            Long restaurantId
    );
}