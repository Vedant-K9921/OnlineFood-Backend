package com.localbites.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(Authentication authentication) {

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    return generateToken(userDetails.getUserId());
}

    public Long getUserIdFromToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public String generateToken(Long userId) {

    Date now = new Date();

    Date expiryDate =
            new Date(now.getTime() + jwtExpiration);

    return Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
}
}