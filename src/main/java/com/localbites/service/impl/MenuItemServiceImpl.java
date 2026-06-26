package com.localbites.service.impl;

import com.localbites.dto.menu.MenuItemRequest;
import com.localbites.dto.menu.MenuItemResponse;
import com.localbites.entity.MenuItem;
import com.localbites.entity.Restaurant;
import com.localbites.entity.User;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.repository.MenuItemRepository;
import com.localbites.repository.RestaurantRepository;
import com.localbites.repository.UserRepository;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Override
    public MenuItemResponse createMenuItem(
            MenuItemRequest request
    ) {

        User currentUser = getCurrentUser();

        Restaurant restaurant =
                restaurantRepository.findById(
                                request.getRestaurantId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Restaurant not found"
                                ));

        validateOwnership(
                restaurant,
                currentUser
        );

        MenuItem menuItem =
                MenuItem.builder()
                        .restaurant(restaurant)
                        .name(request.getName())
                        .description(
                                request.getDescription()
                        )
                        .price(request.getPrice())
                        .imageUrl(
                                request.getImageUrl()
                        )
                        .category(
                                request.getCategory()
                        )
                        .isVeg(
                                request.getIsVeg() == null
                                        ? false
                                        : request.getIsVeg()
                        )
                        .isAvailable(
                                request.getIsAvailable() == null
                                        ? true
                                        : request.getIsAvailable()
                        )
                        .build();

        MenuItem saved =
                menuItemRepository.save(menuItem);

        return mapToResponse(saved);
    }

    @Override
    public MenuItemResponse updateMenuItem(
            Long menuItemId,
            MenuItemRequest request
    ) {

        User currentUser = getCurrentUser();

        MenuItem menuItem =
                menuItemRepository.findById(menuItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Menu item not found"
                                ));

        validateOwnership(
                menuItem.getRestaurant(),
                currentUser
        );

        menuItem.setName(request.getName());
        menuItem.setDescription(
                request.getDescription()
        );
        menuItem.setPrice(
                request.getPrice()
        );
        menuItem.setImageUrl(
                request.getImageUrl()
        );
        menuItem.setCategory(
                request.getCategory()
        );

        if (request.getIsVeg() != null) {
            menuItem.setIsVeg(
                    request.getIsVeg()
            );
        }

        if (request.getIsAvailable() != null) {
            menuItem.setIsAvailable(
                    request.getIsAvailable()
            );
        }

        MenuItem updated =
                menuItemRepository.save(menuItem);

        return mapToResponse(updated);
    }

    @Override
    public void deleteMenuItem(
            Long menuItemId
    ) {

        User currentUser = getCurrentUser();

        MenuItem menuItem =
                menuItemRepository.findById(menuItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Menu item not found"
                                ));

        validateOwnership(
                menuItem.getRestaurant(),
                currentUser
        );

        menuItemRepository.delete(menuItem);
    }

    @Override
    public MenuItemResponse getMenuItemById(
            Long menuItemId
    ) {

        MenuItem menuItem =
                menuItemRepository.findById(menuItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Menu item not found"
                                ));

        return mapToResponse(menuItem);
    }

    @Override
    public List<MenuItemResponse> getMenuByRestaurant(
            Long restaurantId
    ) {

        return menuItemRepository
                .findByRestaurantId(
                        restaurantId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateOwnership(
            Restaurant restaurant,
            User currentUser
    ) {

        if (!restaurant.getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You do not own this restaurant"
            );
        }
    }

    private MenuItemResponse mapToResponse(
            MenuItem menuItem
    ) {

        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .restaurantId(
                        menuItem.getRestaurant()
                                .getId()
                )
                .name(menuItem.getName())
                .description(
                        menuItem.getDescription()
                )
                .price(menuItem.getPrice())
                .imageUrl(
                        menuItem.getImageUrl()
                )
                .category(
                        menuItem.getCategory()
                )
                .isVeg(
                        menuItem.getIsVeg()
                )
                .isAvailable(
                        menuItem.getIsAvailable()
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