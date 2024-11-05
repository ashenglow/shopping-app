package test.shop.infrastructure.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import test.shop.exception.web.CustomAccessDeniedHandler;
import test.shop.exception.web.CustomAuthEntryPointHandler;
import test.shop.infrastructure.persistence.redis.RedisService;
import test.shop.infrastructure.security.filter.JwtAuthFilter;
import test.shop.infrastructure.security.jwt.TokenUtil;
import test.shop.infrastructure.security.filter.MalFormedRequestFilter;
import test.shop.infrastructure.security.service.CustomUserDetailsService;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenUtil tokenUtil;
    private final RedisService redisService;
    private final CustomAuthEntryPointHandler customAuthEntryPointHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private static final String[] AUTH_WHITELIST = {
            "/api/public/**",
            "/monitoring/**",
            "/webjars/**",
             "/h2-console/**",
           "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
            "/favicon.ico",
            "/static/**"
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
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
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

        //CSRF, CORS
        http.csrf(AbstractHttpConfigurer::disable);
//        http.cors(httpSecurityCorsConfigurer ->
//                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
//        );
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
//        http.cors(AbstractHttpConfigurer::disable);
//        http.cors(cors -> {
//            CorsConfigurationSource source = request -> {
//                CorsConfiguration config = new CorsConfiguration();
//                config.setAllowCredentials(true);
//                config.addAllowedOrigin("http://localhost:3000");
//                config.addAllowedHeader("*");
//                config.addAllowedMethod("*");
//                config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
//                return config;
//            };
//            cors.configurationSource(source);
//        });


        //세션 생성, 사용 X
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        FormLogin, BasicHttp 비활성
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);


        // JwtAuthFilter를 UsernamePasswordAuthenticationFilter 전에 추가
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(malformedRequestFilter(), JwtAuthFilter.class);
//        http.addFilterBefore(new OncePerRequestFilter() {
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
//            logger.info("Processing request: " + request.getMethod() + " " + request.getRequestURI());
//            filterChain.doFilter(request, response);
//        }
//    }, UsernamePasswordAuthenticationFilter.class);

        //ExceptionHandler
        http.exceptionHandling((exception) -> exception
                .authenticationEntryPoint(customAuthEntryPointHandler)
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        //인가 설정
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers("/api/public/monitoring/**").permitAll()
                .requestMatchers("/api/public/**","/api/v1/login", "/api/v1/register", "/api/v1/logout", "/api/v1/refresh").permitAll()
                .requestMatchers(("/api/auth/**")).authenticated()
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
