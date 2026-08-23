package com.spring.review.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(
            String username,
            String role
    ) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String generateRefreshToken(
            String username,
            String role
    ) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )
                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String extractUsername(
            String token
    ) {

        return extractClaims(token)
                .getSubject();
    }

    public String extractRole(
            String token
    ) {

        return extractClaims(token)
                .get("role", String.class);
    }

    public String extractTokenType(
            String token
    ) {

        return extractClaims(token)
                .get("type", String.class);
    }

    public boolean isTokenValid(
            String token,
            String username
    ) {

        String tokenUsername =
                extractUsername(token);

        return tokenUsername != null
                && tokenUsername.equals(username)
                && !isTokenExpired(token);
    }

    public boolean isRefreshToken(
            String token
    ) {

        return "refresh".equals(
                extractTokenType(token)
        );
    }

    private boolean isTokenExpired(
            String token
    ) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(
            String token
    ) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}