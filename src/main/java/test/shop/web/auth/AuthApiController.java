package test.shop.web.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.web.dto.ProfileDto;
import test.shop.web.dto.MemberLoginDto;
import test.shop.web.dto.UserModelDto;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final AuthService authService;
    private final TokenUtil tokenUtil;


    @PostMapping("/api/v1/register")
    public ResponseEntity<String> register(@RequestBody ProfileDto request, HttpServletResponse response) throws JsonProcessingException {
        authService.register(request);
        return ResponseEntity.status(200).body("회원이 등록되었습니다.");
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<UserModelDto> login(
            @RequestBody MemberLoginDto request, HttpServletResponse response
    ) throws JsonProcessingException {
        UserModelDto dto = authService.login(request);
        String refreshToken = authService.createRefreshToken(request.getUsername());
        authService.setRefreshTokenToCookie(refreshToken, response);
        log.info("refreshToken : " + refreshToken);
        return ResponseEntity.status(200).body(dto);
    }

    @RequestMapping("/api/v1/refresh")
    public ResponseEntity<UserModelDto> refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String refreshToken = authService.extractRefreshTokenFromCookie(request);
        String username = authService.decodeUsernameFromRefreshToken(refreshToken);
        UserModelDto dto = authService.refresh(username, refreshToken);

        return ResponseEntity.status(200).body(dto);

    }


    @RequestMapping("/api/auth/v1/me")
    public ResponseEntity<UserModelDto> getMemberProfile(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = tokenUtil.extractAccessToken(request);
        UserModelDto dto = authService.getMemberProfile(accessToken);
        return ResponseEntity.status(200).body(dto);
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        String refreshToken = authService.extractRefreshTokenFromCookie(request);
        String accessToken = tokenUtil.extractAccessToken(request);
        authService.logout(accessToken, refreshToken);
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        request.getSession().invalidate();
        response.addCookie(cookie);
        log.info("로그아웃 성공");
        return ResponseEntity.status(200).body("로그아웃 성공");
    }


}
