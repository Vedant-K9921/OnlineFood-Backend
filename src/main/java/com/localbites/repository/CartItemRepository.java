package com.localbites.repository;

import com.localbites.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    @Query("""
            SELECT c
            FROM CartItem c
            JOIN FETCH c.menuItem
            WHERE c.user.id = :userId
            """)
    List<CartItem> findByUserId(
            @Param("userId") Long userId
    );

    @Query("""
            SELECT c
            FROM CartItem c
            JOIN FETCH c.menuItem
            WHERE c.user.id = :userId
            AND c.menuItem.id = :menuItemId
            """)
    Optional<CartItem> findByUserIdAndMenuItemId(
            @Param("userId") Long userId,
            @Param("menuItemId") Long menuItemId
    );

    void deleteByUserId(
            Long userId
    );
}