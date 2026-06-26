package com.localbites.service;

import com.localbites.dto.auth.AuthResponse;
import com.localbites.dto.auth.LoginRequest;
import com.localbites.dto.auth.RegisterRequest;
import com.localbites.dto.auth.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser();
}