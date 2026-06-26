package com.localbites.dto.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;

    private String name;

    private String email;

    private String role;
}