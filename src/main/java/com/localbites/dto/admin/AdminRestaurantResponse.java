package com.localbites.dto.admin;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminRestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private Boolean isOpen;
}