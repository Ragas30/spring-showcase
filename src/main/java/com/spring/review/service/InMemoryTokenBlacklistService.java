package com.spring.review.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnProperty(
        name = "redis.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Map<String, Long> blacklistedTokens =
            new ConcurrentHashMap<>();

    private static final long BLACKLIST_EXPIRY_HOURS = 168;

    private static final long BLACKLIST_EXPIRY_MS =
            BLACKLIST_EXPIRY_HOURS * 60 * 60 * 1000;

    @Override
    public void blacklist(String token) {
        blacklistedTokens.put(
                token,
                System.currentTimeMillis() + BLACKLIST_EXPIRY_MS
        );
    }

    @Override
    public boolean isBlacklisted(String token) {
        Long expiryAt = blacklistedTokens.get(token);

        if (expiryAt == null) {
            return false;
        }

        if (expiryAt < System.currentTimeMillis()) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }

    @Override
    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklistedTokens.entrySet()
                .removeIf(entry -> entry.getValue() < now);
    }
}
