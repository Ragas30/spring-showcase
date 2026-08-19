package com.spring.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testBlacklist_addsToken() {
        String token = "test-jwt-token";

        tokenBlacklistService.blacklist(token);

        verify(valueOperations).set(
                eq("token:blacklist:" + token),
                eq("blacklisted"),
                eq(168L),
                eq(TimeUnit.HOURS)
        );
    }

    @Test
    void testIsBlacklisted_returnsTrueForBlacklistedToken() {
        String token = "blacklisted-token";

        when(redisTemplate.hasKey("token:blacklist:" + token))
                .thenReturn(true);

        boolean result = tokenBlacklistService.isBlacklisted(token);

        assertTrue(result);
    }

    @Test
    void testIsBlacklisted_returnsFalseForNonBlacklistedToken() {
        String token = "non-blacklisted-token";

        when(redisTemplate.hasKey("token:blacklist:" + token))
                .thenReturn(false);

        boolean result = tokenBlacklistService.isBlacklisted(token);

        assertFalse(result);
    }
}
