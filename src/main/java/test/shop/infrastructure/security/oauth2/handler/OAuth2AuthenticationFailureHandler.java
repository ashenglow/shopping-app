package test.shop.infrastructure.security.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import test.shop.infrastructure.security.oauth2.error.OAuth2ErrorType;
import test.shop.infrastructure.security.oauth2.exception.OAuth2AuthenticationProcessingException;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorType;


        if (exception instanceof OAuth2AuthenticationProcessingException){
            OAuth2AuthenticationProcessingException oauth2Exception = (OAuth2AuthenticationProcessingException) exception;
            errorType = oauth2Exception.getErrorType().getCode();
            log.error("OAuth2 authentication failed: {} - Detail: {}",
                    errorType, oauth2Exception.getMessage());

        }else {
            errorType = OAuth2ErrorType.GENERIC_ERROR.getCode();
            log.error("OAuth2 authentication failed with unknown error: {}",
                    exception.getMessage(), exception);
        }
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/oauth2/callback")
                .queryParam("errorType", errorType)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
