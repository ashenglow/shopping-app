package test.shop.infrastructure.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import test.shop.common.exception.CustomRefreshTokenFailException;
import test.shop.infrastructure.security.token.TokenSubject;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    private static final String CACHE_KEY = "CACHE_KEY";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper ObjectMapper;
    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * refresh Token 추출
     *
     * @param request
     * @return
     */

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void save(String userId, String refreshToken, TokenSubject tokenSubject) {

        try {
            hashOperations.put(userId, refreshToken, serializeTokenSubject(tokenSubject));
            redisTemplate.expire(userId, 1, TimeUnit.DAYS);
            log.info("[RedisTemplateService save]refreshToken: {}", refreshToken);
        } catch (Exception e) {
            log.error("[RedisTemplateService save error]: {}", e.getMessage());
        }
    }

    public TokenSubject findByRefreshToken(String userId, String refreshToken) {
        try {
            String value = hashOperations.get(userId, refreshToken);
            log.info("Redis lookup - userId: {}, token: {}", userId, refreshToken);
            log.info("Found value in Redis: {}", value);
            if(value == null) {
                throw new CustomRefreshTokenFailException("Refresh token not found");
            }
            return deserializeTokenSubject(value);
        } catch (Exception e) {
            log.error("[RedisTemplateService findByRefreshToken error]: {}", e.getMessage());
            throw new CustomRefreshTokenFailException("Redis lookup error: " + e.getMessage());
        }
    }

    public Long getMemberId(String refreshToken) {
        try {
            return deserializeTokenSubject(hashOperations.get(CACHE_KEY, refreshToken)).getMemberId();
        } catch (Exception e) {
            log.error("[RedisTemplateService getMemberId error]: {}", e.getMessage());
            return null;
        }
    }

    public boolean existsByRefreshToken(String userId, String refreshToken) {
        try {
            return hashOperations.hasKey(userId, refreshToken);

        } catch (Exception e) {
            log.info("refreshToken: {}", refreshToken);
            log.error("[RedisTemplateService existsByRefreshToken error]: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String decodeUserIdFromRefreshToken(String refreshToken) {
        try {
            Set<String> userIds = redisTemplate.keys("*");
            for (String userId : userIds) {
                String tokenSubjectJson = hashOperations.get(userId, refreshToken);
                if(tokenSubjectJson != null) {
                    TokenSubject tokenSubject = deserializeTokenSubject(tokenSubjectJson);
                    return tokenSubject.getUserId();
                }
            }
            throw new CustomRefreshTokenFailException("Refresh token not found");
        }catch (Exception e) {
            log.error("[RedisTemplateService decodeUserIdFromRefreshToken error]: {}", e.getMessage());
            throw new CustomRefreshTokenFailException("Decode userId from refresh token failed: " + e.getMessage());

        }
    }

    public List<TokenSubject> findAll() {
        try {
            ArrayList<TokenSubject> list = new ArrayList<>();
            Map<String, String> map = hashOperations.entries(CACHE_KEY);
            map.forEach((key, value) -> list.add(deserializeTokenSubject(value)));
            return list;
        } catch (Exception e) {
            log.error("[RedisTemplateService findAll error]: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(String userId, String refreshToken) {
        hashOperations.delete(userId, refreshToken);
        log.info("[RedisTemplateService delete]refreshToken: {}", refreshToken);
    }

    public String getValues(String userId, String key) {
        return hashOperations.get(userId, key);
    }

    public void setValues(String userId, String key, String value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.expire(userId, time, timeUnit);
            hashOperations.put(userId, key, value);
            log.info("[RedisTemplateService setValues]key: {}, value: {}", key, value);
        } catch (Exception e) {
            log.error("[RedisTemplateService setValues error]: {}", e.getMessage());
        }


    }

    public String serializeTokenSubject(TokenSubject tokenSubject) {
        try {
            return ObjectMapper.writeValueAsString(tokenSubject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TokenSubject deserializeTokenSubject(String tokenSubject) {
        try {
            return ObjectMapper.readValue(tokenSubject, TokenSubject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
