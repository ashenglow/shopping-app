package test.shop.infrastructure.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import test.shop.exception.web.CustomAccessDeniedHandler;
import test.shop.exception.web.CustomAuthEntryPointHandler;
import test.shop.infrastructure.oauth2.CustomOAuth2UserService;
import test.shop.infrastructure.oauth2.OAuth2AuthorizationRequestBasedOnCookieRepository;
import test.shop.infrastructure.oauth2.handler.OAuth2AuthenticationFailureHandler;
import test.shop.infrastructure.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import test.shop.infrastructure.security.filter.HeaderCheckFilter;
import test.shop.infrastructure.security.filter.JwtAuthFilter;
import test.shop.infrastructure.security.token.TokenUtil;
import test.shop.infrastructure.security.filter.MalFormedRequestFilter;
import test.shop.infrastructure.security.service.CustomUserDetailsService;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenUtil tokenUtil;
    private final CustomAuthEntryPointHandler customAuthEntryPointHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final HeaderCheckFilter headerCheckFilter;
    private static final String[] AUTH_WHITELIST = {
            "/api/public/**",
            "/monitoring/**",
            "/webjars/**",
            "/h2-console/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico",
            "/static/**",
            "/oauth2/authorization/**",
            "/oauth2/callback/**",
            "/login/oauth2/code/**",
            "/api/v1/oauth2/url/**",
            "/api/v1/login",
            "/api/v1/register",
            "/api/v1/logout",
            "/api/v1/refresh"
    };


    //    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(AUTH_WHITELIST);
//    }
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(customUserDetailsService, tokenUtil, customAuthEntryPointHandler);
    }


    @Bean
    public MalFormedRequestFilter malformedRequestFilter() {
        return new MalFormedRequestFilter();
    }

//    @Bean
//    public RefreshTokenFilter refreshTokenFilter() {
//        return new RefreshTokenFilter(customAuthEntryPointHandler, redisService);
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // OAuth2 설정
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization ->
                        authorization.baseUri("/oauth2/authorization"))
                .redirectionEndpoint(redirection ->
                        redirection.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(userInfo ->
                        userInfo.userService(customOAuth2UserService))
                .failureHandler(new OAuth2AuthenticationFailureHandler())
                .successHandler(oAuth2AuthenticationSuccessHandler)
        );

        // 필터 순서 설정
        http.addFilterBefore(malformedRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        // Exception 처리
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthEntryPointHandler)
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        // 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers("/api/auth/**").authenticated()
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
        );


        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://soolstore.r-e.kr");
        configuration.addAllowedOrigin("http://soolstore.r-e.kr");
        configuration.addAllowedOrigin("https://www.soolstore.r-e.kr");
        configuration.addAllowedOrigin("http://www.soolstore.r-e.kr");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://accounts.google.com");
        configuration.addAllowedOrigin("https://open-api.kakaopay.com");
        configuration.addAllowedOrigin("https://online-pay.kakao.com");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
