package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.config.RateLimitProperties;
import id.my.agungdh.bpkadkepegawaian.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RateLimitProperties rateLimitProperties;

    private static final String RATE_LIMIT_PREFIX = "rate:";

    /**
     * Check login rate limit for an IP address.
     * Key pattern: rate:login:{ip}
     */
    public void checkLoginLimit(String ip) {
        String key = RATE_LIMIT_PREFIX + "login:" + ip;
        checkRateLimit(
                key,
                rateLimitProperties.getLogin().getRequests(),
                rateLimitProperties.getLogin().getTtlSeconds(),
                "Login rate limit exceeded. Please try again later."
        );
    }

    /**
     * Check global rate limit for a user.
     * Key pattern: rate:global:{userId}
     */
    public void checkGlobalLimit(Long userId) {
        String key = RATE_LIMIT_PREFIX + "global:" + userId;
        checkRateLimit(
                key,
                rateLimitProperties.getGlobal().getRequests(),
                rateLimitProperties.getGlobal().getTtlSeconds(),
                "Global rate limit exceeded. Please slow down."
        );
    }

    /**
     * Check endpoint-specific rate limit for a user.
     * Key pattern: rate:endpoint:{userId}:{path}
     */
    public void checkEndpointLimit(Long userId, String path) {
        // Normalize path - remove query parameters and trailing slashes
        String normalizedPath = normalizePath(path);
        String key = RATE_LIMIT_PREFIX + "endpoint:" + userId + ":" + normalizedPath;
        checkRateLimit(
                key,
                rateLimitProperties.getEndpoint().getRequests(),
                rateLimitProperties.getEndpoint().getTtlSeconds(),
                "Endpoint rate limit exceeded. Please slow down."
        );
    }

    /**
     * Core rate limiting logic using Redis INCR + EXPIRE.
     * This is a thread-safe implementation that works with virtual threads.
     */
    private void checkRateLimit(String key, int maxRequests, int ttlSeconds, String errorMessage) {
        // Increment the counter
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == null) {
            // If increment returns null, something went wrong with Redis
            // We'll allow the request to proceed (fail-open)
            log.warn("Redis returned null for rate limit key: {}", key);
            return;
        }

        // Set expiration on first request (when count is 1)
        if (currentCount == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }

        // Check if limit exceeded
        if (currentCount > maxRequests) {
            log.debug("Rate limit exceeded for key: {} (count: {}, max: {})", key, currentCount, maxRequests);
            throw new RateLimitExceededException(errorMessage);
        }

        log.trace("Rate limit check passed for key: {} (count: {}, max: {})", key, currentCount, maxRequests);
    }

    /**
     * Normalize path for consistent rate limiting across similar endpoints.
     * Removes query parameters and trailing slashes.
     */
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        // Remove query parameters
        int queryIndex = path.indexOf('?');
        if (queryIndex > 0) {
            path = path.substring(0, queryIndex);
        }
        // Remove trailing slash unless it's the root path
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
