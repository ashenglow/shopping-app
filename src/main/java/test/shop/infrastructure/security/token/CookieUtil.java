package test.shop.infrastructure.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class CookieUtil {
    private final ObjectMapper objectMapper;

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public Cookie createCookie(String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(604800);
        return cookie;
    }

    public static void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(604800);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public String serialize(Object object) {
        //static일때 objectmapper 사용 불가
        try {
        return Base64.getUrlEncoder()
               .encodeToString(objectMapper.writeValueAsBytes(object));
        }catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }

    }

    public <T> T deserialize(Cookie cookie, Class<T> cls) {
      try {
          return objectMapper.readValue(
                  Base64.getUrlDecoder().decode(cookie.getValue()),
                  cls
          );
      } catch (IOException e){
          throw new RuntimeException("Failed to deserialize cookie value", e);
      }
    }

}
