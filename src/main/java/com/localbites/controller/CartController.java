package com.localbites.controller;

import com.localbites.dto.cart.AddToCartRequest;
import com.localbites.dto.cart.CartResponse;
import com.localbites.dto.cart.UpdateCartRequest;
import com.localbites.response.ApiResponse;
import com.localbites.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request
    ) {

        CartResponse response =
                cartService.addToCart(request);

        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("Item added to cart")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/update/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartRequest request
    ) {

        CartResponse response =
                cartService.updateCartItem(
                        cartItemId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("Cart updated")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/remove/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> removeCartItem(
            @PathVariable Long cartItemId
    ) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Cart item removed")
                        .data("Removed")
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {

        CartResponse response =
                cartService.getCart();

        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("Cart fetched")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> clearCart() {

        cartService.clearCart();

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Cart cleared")
                        .data("Cleared")
                        .build()
        );
    }
}