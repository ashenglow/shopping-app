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
import test.shop.infrastructure.security.jwt.TokenSubject;

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

    public void save(String username, String refreshToken, TokenSubject tokenSubject) {

        try {
            redisTemplate.expire(username, 1, TimeUnit.DAYS);
            hashOperations.put(username, refreshToken, serializeTokenSubject(tokenSubject));
            log.info("[RedisTemplateService save]refreshToken: {}", refreshToken);
        } catch (Exception e) {
            log.error("[RedisTemplateService save error]: {}", e.getMessage());
        }
    }

    public TokenSubject findByRefreshToken(String username, String refreshToken) {
        try {
            return deserializeTokenSubject(hashOperations.get(username, refreshToken));
        } catch (Exception e) {
            log.error("[RedisTemplateService findByRefreshToken error]: {}", e.getMessage());
            return null;
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

    public boolean existsByRefreshToken(String username, String refreshToken) {
        try {
            return hashOperations.hasKey(username, refreshToken);

        } catch (Exception e) {
            log.info("refreshToken: {}", refreshToken);
            log.error("[RedisTemplateService existsByRefreshToken error]: {}", e.getMessage());
            throw new RuntimeException(e);
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

    public void delete(String username, String refreshToken) {
        hashOperations.delete(username, refreshToken);
        log.info("[RedisTemplateService delete]refreshToken: {}", refreshToken);
    }

    public String getValues(String username, String key) {
        return hashOperations.get(username, key);
    }

    public void setValues(String username, String key, String value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.expire(username, time, timeUnit);
            hashOperations.put(username, key, value);
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
