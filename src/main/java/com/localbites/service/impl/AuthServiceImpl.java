package com.localbites.service.impl;

import com.localbites.dto.auth.AuthResponse;
import com.localbites.dto.auth.LoginRequest;
import com.localbites.dto.auth.RegisterRequest;
import com.localbites.dto.auth.UserResponse;
import com.localbites.entity.User;
import com.localbites.exception.BadRequestException;
import com.localbites.repository.UserRepository;
import com.localbites.security.CustomUserDetails;
import com.localbites.security.JwtTokenProvider;
import com.localbites.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        String token =
        jwtTokenProvider.generateToken(
                savedUser.getId()
        );

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        String token =
                jwtTokenProvider.generateToken(authentication);

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(token)
                .user(
                        mapToUserResponse(
                                userDetails.getUser()
                        )
                )
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        return mapToUserResponse(
                userDetails.getUser()
        );
    }

    private UserResponse mapToUserResponse(
            User user
    ) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}