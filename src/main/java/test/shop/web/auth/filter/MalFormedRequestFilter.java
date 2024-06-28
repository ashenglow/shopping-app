package test.shop.web.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class MalFormedRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(MalFormedRequestFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
         if (method.startsWith("MGLNDD_") || !method.matches("^[A-Z]+$")) {
            logger.warning(String.format("Blocked suspicious request: Method '%s' from IP %s", method, request.getRemoteAddr()));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request method");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
