package test.shop.infrastructure.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import test.shop.exception.web.CustomTokenException;
import test.shop.domain.model.member.MemberType;
import test.shop.exception.web.CustomRefreshTokenFailException;
import test.shop.infrastructure.persistence.redis.RedisService;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class TokenUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpTime;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    public TokenUtil(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long accessTokenExpTime, ObjectMapper objectMapper, RedisService redisService) {
        this.redisService = redisService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime * 60;
        this.objectMapper = objectMapper;
    }



    /**
     * Access token 재발급
     */
    public String reissueAccessToken(TokenSubject subject) throws JsonProcessingException {

      ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validTime = now.plusSeconds(accessTokenExpTime);
        String subjectStr = new ObjectMapper().writeValueAsString(subject);

        return Jwts.builder()
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(validTime.toInstant()))
                .claim("subject", subjectStr)
                .signWith(secretKey)
                .compact();

    }




    /**
     * JWT 생성
     */
    public String createToken(Long memberId, String userId, MemberType memberType) throws JsonProcessingException {
        TokenSubject subject = new TokenSubject(memberId, userId, memberType);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validTime = now.plusSeconds(accessTokenExpTime);
        String subjectStr = new ObjectMapper().writeValueAsString(subject);
        log.info("validTime: {}", validTime);
        return Jwts.builder()
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(validTime.toInstant()))
                .claim("subject", subjectStr)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 파싱
     *
     * @param accessToken
     * @return claims
     */
    public Claims parseToken(String accessToken) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(accessToken);
        return claimsJws.getPayload();

    }

    /**
     * JWT 유효성 검사
     *
     * @param accessToken
     * @return isValid
     */
    public boolean validateToken(String userId, String accessToken) {
        try {
            if(redisService.getValues(userId, accessToken) != null
             && redisService.getValues(userId, accessToken).equals("logout") ) {
                return false;
            }
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
            log.info("claims: {}", claims);
            log.info("expiration: {}", claims.getExpiration());
            log.info("boolean: {}", claims.getExpiration().before(new Date()));
            return claims.getExpiration().before(new Date());
        } catch (SignatureException e) {
            log.info("Invalid JWT signature");
            throw new JwtException("Invalid JWT signature" + e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
            throw new JwtException("Expired JWT token" + e.getMessage());
        } catch (MalformedJwtException e) {
            log.info("Malformed JWT token");
            throw new JwtException("Malformed JWT token" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
            throw new JwtException("Unsupported JWT token" + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty");
            throw new JwtException("JWT claims string is empty" + e.getMessage());
        }
    }

    public Authentication getAuthentication(String accessToken) {
        try {
            String subjectStr = parseToken(accessToken).get("subject", String.class);
            TokenSubject subject = new ObjectMapper().readValue(subjectStr, TokenSubject.class);
            String role = subject.getMemberType().getRoleName();

            return new UsernamePasswordAuthenticationToken(subject.getUserId(), null, Collections.singletonList(new SimpleGrantedAuthority(role)));

        } catch (Exception e) {
            throw new IllegalArgumentException("Access token not found");
        }
    }

    /**
     * JWT에서 회원 ID 추출
     *
     * @param accessToken
     * @return memberId
     */
    public Long getMemberId(String accessToken){
        try {
            String subjectStr = parseToken(accessToken).get("subject", String.class);
            log.info("subjectStr: {}", subjectStr);
            TokenSubject subject = new ObjectMapper().readValue(subjectStr, TokenSubject.class);
            log.info("memberId: {}", subject.getMemberId());
            return subject.getMemberId();
        }catch (JsonProcessingException e) {
            throw new CustomTokenException("Failed to parse token payload");
        }

    }

    public String getUserId(String accessToken) {
        try {
            String subjectStr = parseToken(accessToken).get("subject", String.class);
            TokenSubject subject = new ObjectMapper().readValue(subjectStr, TokenSubject.class);
            return subject.getUserId();
        } catch (JsonProcessingException e) {
            throw new CustomTokenException("Failed to parse token payload");
        }

    }

    /**
     * header에서 access token 추출
     */
    public String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = "";
        try {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token =  bearerToken.substring(7);
        }
        }catch (Exception e) {
            log.info("Token not exists");
            throw new JwtException("Token not exists" + e.getMessage());
        }

        return token;
    }


    /**
     * TokenSubject 추출
     */
    public TokenSubject extractTokenSubject(String accessToken) {

        return objectMapper.convertValue(parseToken(accessToken).get("subject"), TokenSubject.class);
    }

    /**
     * AccessToken Expiration
     *
     * @return accessTokenExpiration
     */
    public long getAccessTokenExpirationMillis(String accessToken) {
        return parseToken(accessToken).getExpiration().getTime() - System.currentTimeMillis();
    }


}


