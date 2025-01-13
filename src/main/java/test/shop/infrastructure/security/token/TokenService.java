package test.shop.infrastructure.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.shop.domain.model.member.Member;
import test.shop.domain.model.member.MemberType;
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
       String refreshToken = UUID.randomUUID().toString();
       redisService.save(
               member.getUserId(),
               refreshToken,
               new TokenSubject(member.getId(), member.getUserId(), member.getMemberType() )
       );
        return refreshToken;

    }

}
