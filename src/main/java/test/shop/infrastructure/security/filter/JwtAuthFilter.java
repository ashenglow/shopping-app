package test.shop.infrastructure.security.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import test.shop.common.exception.CustomAuthEntryPointHandler;
import test.shop.infrastructure.security.service.CustomUserDetailsService;
import test.shop.infrastructure.security.token.TokenUtil;

import java.io.IOException;
import java.util.Set;


@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenUtil tokenUtil;
    private final CustomAuthEntryPointHandler customAuthEntryPointHandler;

    private static final Set<String> WHITELIST = Set.of(
             "/api/v1/login",
            "/api/v1/register",
            "/api/v1/logout",
            "/api/v1/refresh",
            "/h2-console",
            "/webjars",
             "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui.html",
            "/api/public",
            "/monitoring",
            "/favicon.ico",
            "/static",
            "/oauth2/authorization",    // Add OAuth2 authorization endpoint
            "/oauth2/callback",     // Add OAuth2 callback endpoint
            "/api/v1/oauth2/url",
            "/login/oauth2/code"




    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return WHITELIST.stream().anyMatch(path::startsWith)
                || path.contains("oauth2")
                || path.contains("login/oauth2/code");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestURI = request.getRequestURI();
        log.info("Processing request: {}", requestURI);

        try {
  //request header 에서 token 추출
            String accessToken = tokenUtil.extractAccessToken(request);
            String userId = tokenUtil.getUserId(accessToken);

            if (accessToken != null && !tokenUtil.validateToken(userId, accessToken)) {
                Long memberId = tokenUtil.getMemberId(accessToken);
                log.debug("Extracted memberId from token: {}", memberId);

                //user와 토큰 일치 시 userDetails 생성
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(memberId.toString());

                if (userDetails != null) {
                    log.info(userDetails.toString());
                    //UserDetails, password, role -> 접근권한 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //SecurityContext에 접근 권한 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }

            }



        } catch (JwtException e) {
            //JWT-related exception 발생 시
            customAuthEntryPointHandler.commence(request, response, new BadCredentialsException("JWT validation failed: " + e.getMessage()) {
            });
            return;
        }
        filterChain.doFilter(request, response);

    }


}
