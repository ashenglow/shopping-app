package test.shop.infrastructure.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import test.shop.domain.model.member.Member;
import test.shop.infrastructure.oauth2.CustomOAuth2User;
import test.shop.infrastructure.oauth2.OAuth2AuthorizationRequestBasedOnCookieRepository;
import test.shop.infrastructure.oauth2.util.CookieUtil;
import test.shop.infrastructure.persistence.redis.RedisService;
import test.shop.infrastructure.security.token.TokenService;
import test.shop.infrastructure.security.token.TokenSubject;
import test.shop.infrastructure.security.token.TokenUtil;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenUtil tokenUtil;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Member member = oAuth2User.getMember();
        String userId = member.getUserId();
        String nickname = member.getNickname();
        String frontendUrl = "http://localhost:3000";
        try {
            String accessToken = tokenUtil.createToken(
                    member.getId(),
                    userId,
                    member.getMemberType()
            );
            String refreshToken = tokenService.createAndSaveRefreshToken(member);

            // Set tokens
//            response.setHeader("Authorization", "Bearer " + accessToken);
//            CookieUtil.addCookie(response, "refreshToken", refreshToken, 604800);
//            String targetUrl = CookieUtil.getCookie(request, "redirect_uri")
//                    .map(Cookie::getValue)
//                    .orElse(getDefaultTargetUrl());
//
//            authorizationRequestRepository.removeCookies(request, response);
//
//            getRedirectStrategy().sendRedirect(request,response,
//                    UriComponentsBuilder.fromUriString(targetUrl)
//                            .queryParam("token", accessToken)
//                            .queryParam("userId", userId)
//                            .queryParam("nickname", nickname)
//                            .build().toUriString());
            Cookie cookie = cookieUtil.createCookie("refreshToken", refreshToken);
            response.addCookie(cookie);

            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/oauth2/callback")
                    .queryParam("token", accessToken)
                    .queryParam("userId", userId)
                    .queryParam("nickname", nickname)
                    .build().toUriString();

            authorizationRequestRepository.removeAuthorizationRequest(request, response);

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            throw new IOException("Failed to process OAuth2AuthenticationSuccessHandler.", e);
        }
    }
}
