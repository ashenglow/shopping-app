package test.shop.exception.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import test.shop.application.dto.response.ErrorResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Component
public class CustomAuthEntryPointHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Not Authenticated Request: {}", authException.getMessage());
        ErrorResponseDto dto = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), authException.getMessage(), LocalDateTime.now());

        String responseBody = objectMapper.writeValueAsString(dto);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
