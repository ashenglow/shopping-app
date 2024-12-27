package test.shop.infrastructure.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.shop.domain.model.member.Member;
import test.shop.infrastructure.oauth2.util.CookieUtil;
import test.shop.infrastructure.persistence.redis.RedisService;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public String createAndSaveRefreshToken(Member member) throws JsonProcessingException {
        String userId = member.getUserId();
        TokenData tokenData = new TokenData(
                UUID.randomUUID().toString(),
                userId
        );
        String tokenJson = objectMapper.writeValueAsString(tokenData);
        String refreshToken = Base64.getEncoder().encodeToString(tokenJson.getBytes());
        redisService.save(userId, refreshToken, new TokenSubject(member.getId(),userId, member.getMemberType()));
        return refreshToken;

    }

}
