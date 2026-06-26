package com.localbites.service;

import com.localbites.dto.cart.AddToCartRequest;
import com.localbites.dto.cart.CartResponse;
import com.localbites.dto.cart.UpdateCartRequest;

public interface CartService {

    CartResponse addToCart(
            AddToCartRequest request
    );

    CartResponse updateCartItem(
            Long cartItemId,
            UpdateCartRequest request
    );

    void removeCartItem(
            Long cartItemId
    );

    CartResponse getCart();

    void clearCart();
}