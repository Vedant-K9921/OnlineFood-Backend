package com.localbites.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
    @Table(
    name = "menu_items",
    indexes = {
        @Index(name = "idx_menu_restaurant", columnList = "restaurant_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false,length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;

    @Column(length = 100)
    private String category;

    private String imageUrl;

    @Builder.Default
    private Boolean isVeg = false;

    @Builder.Default
    private Boolean isAvailable = true;

}