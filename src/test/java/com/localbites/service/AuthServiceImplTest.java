package com.localbites.service;

import com.localbites.dto.auth.RegisterRequest;
import com.localbites.entity.User;
import com.localbites.enums.Role;
import com.localbites.repository.UserRepository;
import com.localbites.security.JwtTokenProvider;
import com.localbites.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtTokenProvider jwtTokenProvider;

    @InjectMocks AuthServiceImpl service;

    @Test
    void publicRegistrationAlwaysCreatesCustomer() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Attacker");
        request.setEmail("attacker@example.com");
        request.setPassword("password123");
        request.setPhone("9999999999");
        request.setRole(Role.ROLE_ADMIN);

        when(userRepository.existsByEmail("attacker@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtTokenProvider.generateToken(1L)).thenReturn("token");

        var response = service.register(request);

        assertEquals(Role.ROLE_CUSTOMER, response.getUser().getRole());
        verify(userRepository).save(argThat(user -> user.getRole() == Role.ROLE_CUSTOMER));
    }
}
