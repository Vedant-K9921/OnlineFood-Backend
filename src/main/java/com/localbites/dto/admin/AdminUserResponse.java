package com.localbites.dto.admin;

import com.localbites.enums.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
}
