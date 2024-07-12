package test.shop.web.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.filter.OncePerRequestFilter;
import test.shop.exception.web.CustomAccessDeniedHandler;
import test.shop.exception.web.CustomAuthEntryPointHandler;
import test.shop.web.auth.RedisService;
import test.shop.web.auth.filter.JwtAuthFilter;
import test.shop.web.auth.TokenUtil;
import test.shop.web.auth.filter.MalFormedRequestFilter;


import java.io.IOException;
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
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger/**",
            "/swagger-ui/**",
            "/h2-console/**",
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
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/v1/login", "/api/v1/register", "/api/v1/logout", "/api/v1/refresh").permitAll()
                .requestMatchers(("/api/**")).authenticated()
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()

        );


        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://d17cqt7ozoigka.cloudfront.net");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
