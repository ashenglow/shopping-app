package test.shop.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class MalFormedRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(MalFormedRequestFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        if (method.startsWith("MGLNDD_") || !method.matches("^[A-Z]+$")) {
            logger.warn("Blocked suspicious request: Method '{}' from IP {}", method, request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request method");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
