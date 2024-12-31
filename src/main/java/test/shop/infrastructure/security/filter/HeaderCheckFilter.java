package test.shop.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class HeaderCheckFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request URI: {}", request.getRequestURI());
        log.info("X-Forwarded-Proto: {}", request.getHeader("X-Forwarded-Proto"));
        log.info("X-Forwarded-Host: {}", request.getHeader("X-Forwarded-Host"));
        log.info("X-Forwarded-Port: {}", request.getHeader("X-Forwarded-Port"));
        log.info("Scheme: {}", request.getScheme());

        filterChain.doFilter(request, response);
    }
}
