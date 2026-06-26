package com.localbites.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "restaurants",
    indexes = {
        @Index(name = "idx_restaurant_owner", columnList = "owner_id"),
        @Index(name = "idx_restaurant_name", columnList = "name")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false,length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    private String imageUrl;

    @Column(length = 100)
    private String cuisineType;

    @Builder.Default
    private Boolean isOpen = true;

    @Builder.Default
    private Double rating = 0.0;

}