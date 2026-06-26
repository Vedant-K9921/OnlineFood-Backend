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
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.repository.CartItemRepository;
import com.localbites.repository.OrderRepository;
import com.localbites.repository.UserRepository;
import com.localbites.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.localbites.service.OrderTrackingService;

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
public OrderResponse placeOrder(
        Long userId,
        PlaceOrderRequest request
) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User not found"
                    ));

    List<CartItem> cartItems =
            cartItemRepository.findByUserId(userId);

    if (cartItems.isEmpty()) {
        throw new RuntimeException(
                "Cart is empty"
        );
    }

    Restaurant restaurant =
            cartItems.get(0)
                    .getMenuItem()
                    .getRestaurant();

    BigDecimal totalAmount =
            BigDecimal.ZERO;

    List<OrderItem> orderItems =
            new ArrayList<>();

    Order order = Order.builder()
            .user(user)
            .restaurant(restaurant)
            .deliveryAddress(
                    request.getDeliveryAddress()
            )
            .status(OrderStatus.PLACED)
            .paymentStatus(
                    PaymentStatus.PENDING
            )
            .build();

                for (CartItem cartItem : cartItems) {

        MenuItem menuItem =
                cartItem.getMenuItem();

        BigDecimal itemTotal =
                menuItem.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

        totalAmount =
                totalAmount.add(itemTotal);

        OrderItem orderItem =
                OrderItem.builder()
                        .order(order)
                        .menuItem(menuItem)
                        .quantity(
                                cartItem.getQuantity()
                        )
                        .priceAtOrder(
                                menuItem.getPrice()
                        )
                        .build();

        orderItems.add(orderItem);
    }
        order.setTotalAmount(
            totalAmount
    );

    order.setOrderItems(
            orderItems
    );

    Order savedOrder =
            orderRepository.save(order);

    cartItemRepository.deleteAll(
            cartItems
    );

    return mapToResponse(
            savedOrder
    );
}

@Override
@Transactional(readOnly = true)
public List<OrderResponse> getMyOrders(
        Long userId
) {

    return orderRepository
            .findByUserIdOrderByCreatedAtDesc(
                    userId
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}

@Override
@Transactional(readOnly = true)
public OrderResponse getOrderById(
        Long orderId,
        Long userId
) {

    Order order =
            orderRepository.findById(orderId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Order not found"
                            ));

    if (!order.getUser().getId()
            .equals(userId)) {

        throw new RuntimeException(
                "Access denied"
        );
    }

    return mapToResponse(order);
}

@Override
@Transactional(readOnly = true)
public List<OrderResponse>
getRestaurantOrders(
        Long restaurantId
) {

    return orderRepository
            .findByRestaurantIdOrderByCreatedAtDesc(
                    restaurantId
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}

@Override
public OrderResponse updateOrderStatus(
        Long orderId,
        OrderStatus status
) {

    Order order =
            orderRepository.findById(orderId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Order not found"
                            ));

    order.setStatus(status);

    Order savedOrder =
            orderRepository.save(order);

    orderTrackingService.broadcastOrderStatus(
            savedOrder.getId(),
            savedOrder.getStatus()
    );

    return mapToResponse(savedOrder);
}

private OrderResponse mapToResponse(
        Order order
) {

    List<OrderItemResponse> items =
            order.getOrderItems()
                    .stream()
                    .map(item ->
                            OrderItemResponse.builder()
                                    .menuItemId(
                                            item.getMenuItem().getId()
                                    )
                                    .menuItemName(
                                            item.getMenuItem().getName()
                                    )
                                    .quantity(
                                            item.getQuantity()
                                    )
                                    .priceAtOrder(
                                            item.getPriceAtOrder()
                                    )
                                    .subtotal(
                                            item.getPriceAtOrder()
                                                    .multiply(
                                                            BigDecimal.valueOf(
                                                                    item.getQuantity()
                                                            )
                                                    )
                                    )
                                    .build()
                    )
                    .toList();

    return OrderResponse.builder()
            .id(order.getId())
            .userId(
                    order.getUser().getId()
            )
            .restaurantId(
                    order.getRestaurant().getId()
            )
            .deliveryAddress(
                    order.getDeliveryAddress()
            )
            .status(
                    order.getStatus()
            )
            .paymentStatus(
                    order.getPaymentStatus()
            )
            .totalAmount(
                    order.getTotalAmount()
            )
            .createdAt(
                    order.getCreatedAt()
            )
            .items(items)
            .build();
}
}