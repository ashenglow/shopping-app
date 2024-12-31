package test.shop.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;


@Component
public class ExtensiveHeaderDebugFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ExtensiveHeaderDebugFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Enumeration<String> headerNames = request.getHeaderNames();
        log.error("===== ALL HEADERS START =====");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.error("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        log.error("===== ALL HEADERS END =====");

        log.error("Request URI: {}", request.getRequestURI());
        log.error("Request Scheme: {}", request.getScheme());
        log.error("Is Secure: {}", request.isSecure());

        chain.doFilter(request, response);
    }
}
