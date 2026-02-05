package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.dto.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${application.security.token.ttl:86400}")
    private long tokenTtl;

    private static final String TOKEN_PREFIX = "auth:";

    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public void storeToken(String token, UserData userData) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userData, tokenTtl, TimeUnit.SECONDS);
        log.debug("Stored token for user: {}", userData.getUsername());
    }

    public boolean validateToken(String token) {
        String key = TOKEN_PREFIX + token;
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    public UserData getUserData(String token) {
        String key = TOKEN_PREFIX + token;
        Object data = redisTemplate.opsForValue().get(key);
        if (data instanceof UserData) {
            return (UserData) data;
        }
        return null;
    }

    public void revokeToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
        log.debug("Revoked token");
    }

    public String refreshToken(String oldToken) {
        UserData userData = getUserData(oldToken);
        if (userData == null) {
            return null;
        }

        revokeToken(oldToken);

        String newToken = generateToken();
        storeToken(newToken, userData);
        return newToken;
    }

    public Duration getTokenTtl(String token) {
        String key = TOKEN_PREFIX + token;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0) {
            return Duration.ofSeconds(ttl);
        }
        return Duration.ZERO;
    }
}
