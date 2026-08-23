package com.spring.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    private static final long BLACKLIST_EXPIRY_HOURS = 168;

    @Override
    public void blacklist(String token) {
        ValueOperations<String, String> ops =
                redisTemplate.opsForValue();

        ops.set(
                BLACKLIST_PREFIX + token,
                "blacklisted",
                BLACKLIST_EXPIRY_HOURS,
                TimeUnit.HOURS
        );
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(BLACKLIST_PREFIX + token)
        );
    }

    @Override
    public void cleanupExpiredTokens() {
        var keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
