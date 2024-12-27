package test.shop.infrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import test.shop.domain.repository.MemberRepository;
import test.shop.infrastructure.oauth2.CustomOAuth2UserService;
import test.shop.infrastructure.oauth2.OAuth2AuthorizationRequestBasedOnCookieRepository;
import test.shop.infrastructure.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import test.shop.infrastructure.oauth2.util.CookieUtil;
import test.shop.infrastructure.persistence.redis.RedisService;
import test.shop.infrastructure.security.token.TokenService;
import test.shop.infrastructure.security.token.TokenUtil;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(memberRepository, passwordEncoder);
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(
            TokenUtil tokenUtil,
            TokenService tokenService,
            CookieUtil cookieUtil,
            OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRepository
    ) {
        return new OAuth2AuthenticationSuccessHandler(tokenUtil, tokenService, cookieUtil, authorizationRepository);
    }
}
