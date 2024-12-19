package test.shop.infrastructure.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import test.shop.domain.model.member.Member;
import test.shop.infrastructure.oauth2.CustomOAuth2User;
import test.shop.infrastructure.oauth2.OAuth2AuthorizationRequestBasedOnCookieRepository;
import test.shop.infrastructure.oauth2.util.CookieUtil;
import test.shop.infrastructure.persistence.redis.RedisService;
import test.shop.infrastructure.security.jwt.TokenSubject;
import test.shop.infrastructure.security.jwt.TokenUtil;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenUtil tokenUtil;
    private final RedisService redisService;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Member member = oAuth2User.getMember();
        String userId = member.getUserId();
        try {
            String accessToken = tokenUtil.createToken(
                    member.getId(),
                    userId,
                    member.getMemberType()
            );

            String tokenData = UUID.randomUUID() + ":" + userId;
            String refreshToken = Base64.getEncoder().encodeToString(tokenData.getBytes());
            redisService.save(
                    userId,
                    refreshToken,
                    new TokenSubject(member.getId(), userId, member.getMemberType()));

            // Set tokens
            response.setHeader("Authorization", "Bearer " + accessToken);
            CookieUtil.addCookie(response, "refreshToken", refreshToken, 604800);
            String targetUrl = CookieUtil.getCookie(request, "redirect_uri")
                    .map(Cookie::getValue)
                    .orElse(getDefaultTargetUrl());



        }
    }
}
