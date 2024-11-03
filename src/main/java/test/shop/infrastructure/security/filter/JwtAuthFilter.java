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
import test.shop.exception.web.CustomAuthEntryPointHandler;
import test.shop.infrastructure.security.service.CustomUserDetailsService;
import test.shop.infrastructure.security.jwt.TokenUtil;

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
            "/h2-console/**",
            "/webjars/**",
             "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui.html"


    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // Check for exact matches in the whitelist
    if (WHITELIST.contains(requestURI)  || requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs/") || requestURI.startsWith("/api/public/")) {
        log.info("Skipping JwtAuthFilter for whitelisted URI: {}", requestURI);
        filterChain.doFilter(request, response);
        return;
    }
        try {
  //request header 에서 token 추출
            String accessToken = tokenUtil.extractAccessToken(request);
            String username = tokenUtil.getUsername(accessToken);
            if (accessToken != null && !tokenUtil.validateToken(username, accessToken)) {
                Long memberId = tokenUtil.getMemberId(accessToken);
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
