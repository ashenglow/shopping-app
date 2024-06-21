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
import test.shop.web.dto.ErrorResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Component
public class CustomRefreshTokenFailException extends RuntimeException {

    public CustomRefreshTokenFailException(String message) {
        super(message);
    }

    public CustomRefreshTokenFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomRefreshTokenFailException(Throwable cause) {
        super(cause);
    }

    protected CustomRefreshTokenFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
