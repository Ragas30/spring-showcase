package com.spring.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "test-secret-key-for-unit-testing-minimum-32-characters";
    private static final long EXPIRATION = 86400000L;
    private static final long REFRESH_EXPIRATION = 604800000L;
    private static final String USERNAME = "testuser";
    private static final String ROLE = "ADMIN";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Test
    void testGenerateToken_validInput_returnsToken() {
        String token = jwtService.generateToken(USERNAME, ROLE);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername_fromValidToken_returnsUsername() {
        String token = jwtService.generateToken(USERNAME, ROLE);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    void testExtractRole_fromValidToken_returnsRole() {
        String token = jwtService.generateToken(USERNAME, ROLE);

        String extractedRole = jwtService.extractRole(token);

        assertEquals(ROLE, extractedRole);
    }

    @Test
    void testIsTokenValid_withCorrectUsername_returnsTrue() {
        String token = jwtService.generateToken(USERNAME, ROLE);

        boolean valid = jwtService.isTokenValid(token, USERNAME);

        assertTrue(valid);
    }

    @Test
    void testIsTokenValid_withWrongUsername_returnsFalse() {
        String token = jwtService.generateToken(USERNAME, ROLE);

        boolean valid = jwtService.isTokenValid(token, "wronguser");

        assertFalse(valid);
    }

    @Test
    void testIsRefreshToken_withRefreshToken_returnsTrue() {
        String refreshToken = jwtService.generateRefreshToken(USERNAME, ROLE);

        boolean isRefresh = jwtService.isRefreshToken(refreshToken);

        assertTrue(isRefresh);
    }

    @Test
    void testIsRefreshToken_withAccessToken_returnsFalse() {
        String accessToken = jwtService.generateToken(USERNAME, ROLE);

        boolean isRefresh = jwtService.isRefreshToken(accessToken);

        assertFalse(isRefresh);
    }

    @Test
    void testGenerateRefreshToken_validInput_returnsToken() {
        String token = jwtService.generateRefreshToken(USERNAME, ROLE);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(USERNAME, jwtService.extractUsername(token));
        assertEquals(ROLE, jwtService.extractRole(token));
        assertEquals("refresh", jwtService.extractTokenType(token));
    }
}
