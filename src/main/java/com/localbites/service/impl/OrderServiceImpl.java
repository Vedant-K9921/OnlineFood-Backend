package com.localbites.service.impl;

import com.localbites.dto.order.OrderItemResponse;
import com.localbites.dto.order.OrderResponse;
import com.localbites.dto.order.PlaceOrderRequest;
import com.localbites.entity.CartItem;
import com.localbites.entity.MenuItem;
import com.localbites.entity.Order;
import com.localbites.entity.OrderItem;
import com.localbites.entity.Restaurant;
import com.localbites.entity.User;
import com.localbites.enums.OrderStatus;
import com.localbites.enums.PaymentStatus;
import com.localbites.exception.BadRequestException;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.exception.UnauthorizedException;
import com.localbites.repository.CartItemRepository;
import com.localbites.repository.OrderRepository;
import com.localbites.repository.UserRepository;
import com.localbites.service.OrderService;
import com.localbites.service.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final OrderTrackingService orderTrackingService;

    @Override
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Restaurant restaurant = cartItems.get(0).getMenuItem().getRestaurant();
        if (!Boolean.TRUE.equals(restaurant.getIsOpen())) {
            throw new BadRequestException("Restaurant is currently closed");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = cartItem.getMenuItem();

            if (!restaurant.getId().equals(menuItem.getRestaurant().getId())) {
                throw new BadRequestException("Cart contains items from multiple restaurants");
            }
            if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
                throw new BadRequestException("Menu item is unavailable: " + menuItem.getName());
            }
            if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
                throw new BadRequestException("Invalid cart quantity");
            }

            BigDecimal price = menuItem.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            orderItems.add(OrderItem.builder()
                    .order(null)
                    .menuItem(menuItem)
                    .quantity(cartItem.getQuantity())
                    .priceAtOrder(price)
                    .build());
        }

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .deliveryAddress(request.getDeliveryAddress().trim())
                .status(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(totalAmount)
                .orderItems(orderItems)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied");
        }
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getRestaurantOrders(Long restaurantId) {
        User owner = getCurrentUser();
        Restaurant restaurant = getRestaurant(restaurantId);
        verifyOwner(restaurant, owner);

        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        User owner = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        verifyOwner(order.getRestaurant(), owner);
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        orderTrackingService.broadcastOrderStatus(savedOrder.getId(), savedOrder.getStatus());
        return mapToResponse(savedOrder);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            throw new BadRequestException("Order is already in status " + current);
        }
        boolean valid = switch (current) {
            case PLACED -> next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING -> next == OrderStatus.OUT_FOR_DELIVERY || next == OrderStatus.CANCELLED;
            case OUT_FOR_DELIVERY -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        if (!valid) {
            throw new BadRequestException("Invalid order status transition: " + current + " -> " + next);
        }
    }

    private Restaurant getRestaurant(Long restaurantId) {
        return orderRepository.findRestaurantById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    private void verifyOwner(Restaurant restaurant, User owner) {
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You do not own this restaurant");
        }
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.localbites.security.CustomUserDetails userDetails =
                (com.localbites.security.CustomUserDetails) authentication.getPrincipal();
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .priceAtOrder(item.getPriceAtOrder())
                        .subtotal(item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .restaurantId(order.getRestaurant().getId())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
