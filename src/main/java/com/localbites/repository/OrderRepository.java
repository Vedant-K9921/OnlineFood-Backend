package com.localbites.repository;

import com.localbites.entity.Order;
import com.localbites.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Order> findByRestaurantIdOrderByCreatedAtDesc(
            Long restaurantId
    );

    Long countByRestaurantId(Long restaurantId);

    Long countByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status
    );

    @Query("""
    SELECT COALESCE(SUM(o.totalAmount),0)
    FROM Order o
    WHERE o.restaurant.id = :restaurantId
    AND o.paymentStatus = 'PAID'
    """)
    BigDecimal getTotalRevenue(
            @Param("restaurantId")
            Long restaurantId
    );

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        WHERE o.paymentStatus = 'PAID'
        """)
        BigDecimal getPlatformRevenue();
}