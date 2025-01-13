package test.shop.infrastructure.oauth2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import test.shop.infrastructure.oauth2.error.OAuth2ErrorType;

@Slf4j
public class OAuth2AuthenticationProcessingException extends OAuth2AuthenticationException {
    private final OAuth2ErrorType errorType;
    private final String errorMessage;

    public OAuth2AuthenticationProcessingException(OAuth2ErrorType errorType, String errorMessage) {
        super(new OAuth2Error("authentication_error"), errorType.getCode());
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    public OAuth2ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String getMessage() {
        return String.format("%s - Detail: %s", errorType.getCode(), errorMessage);
    }
}
