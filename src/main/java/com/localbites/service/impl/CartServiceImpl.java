package com.localbites.service.impl;

import com.localbites.dto.cart.AddToCartRequest;
import com.localbites.dto.cart.CartItemResponse;
import com.localbites.dto.cart.CartResponse;
import com.localbites.dto.cart.UpdateCartRequest;
import com.localbites.entity.CartItem;
import com.localbites.entity.MenuItem;
import com.localbites.entity.User;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.repository.CartItemRepository;
import com.localbites.repository.MenuItemRepository;
import com.localbites.repository.UserRepository;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponse addToCart(
            AddToCartRequest request
    ) {

        User currentUser = getCurrentUser();

        MenuItem menuItem =
                menuItemRepository.findById(
                                request.getMenuItemId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Menu item not found"
                                ));

        CartItem cartItem =
                cartItemRepository
                        .findByUserIdAndMenuItemId(
                                currentUser.getId(),
                                menuItem.getId()
                        )
                        .orElse(null);

        if (cartItem != null) {

            cartItem.setQuantity(
                    cartItem.getQuantity()
                            + request.getQuantity()
            );

        } else {

            cartItem = CartItem.builder()
                    .user(currentUser)
                    .menuItem(menuItem)
                    .quantity(
                            request.getQuantity()
                    )
                    .build();
        }

        cartItemRepository.save(cartItem);

        return getCart();
    }

    @Override
    public CartResponse updateCartItem(
            Long cartItemId,
            UpdateCartRequest request
    ) {

        User currentUser = getCurrentUser();

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found"
                                ));

        if (!cartItem.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "Unauthorized cart access"
            );
        }

        cartItem.setQuantity(
                request.getQuantity()
        );

        cartItemRepository.save(cartItem);

        return getCart();
    }

    @Override
    public void removeCartItem(
            Long cartItemId
    ) {

        User currentUser = getCurrentUser();

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found"
                                ));

        if (!cartItem.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "Unauthorized cart access"
            );
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    @Override
    public CartResponse getCart() {

        User currentUser = getCurrentUser();

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(
                        currentUser.getId()
                );

        List<CartItemResponse> items =
                cartItems.stream()
                        .map(this::mapToResponse)
                        .toList();

        BigDecimal total =
                items.stream()
                        .map(
                                CartItemResponse::getSubtotal
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return CartResponse.builder()
                .items(items)
                .totalAmount(total)
                .build();
    }

    @Override
    public void clearCart() {

        User currentUser = getCurrentUser();

        cartItemRepository.deleteByUserId(
                currentUser.getId()
        );
    }

    private CartItemResponse mapToResponse(
            CartItem cartItem
    ) {

        BigDecimal subtotal =
                cartItem.getMenuItem()
                        .getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .menuItemId(
                        cartItem.getMenuItem().getId()
                )
                .menuItemName(
                        cartItem.getMenuItem().getName()
                )
                .quantity(
                        cartItem.getQuantity()
                )
                .price(
                        cartItem.getMenuItem()
                                .getPrice()
                )
                .subtotal(subtotal)
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