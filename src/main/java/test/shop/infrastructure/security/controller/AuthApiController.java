package test.shop.infrastructure.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.application.dto.request.MemberJoinRequestDto;
import test.shop.infrastructure.security.token.TokenUtil;
import test.shop.application.dto.response.MemberLoginDto;
import test.shop.application.dto.response.UserModelDto;
import test.shop.infrastructure.security.service.AuthService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Auth 관련 API 입니다")
public class AuthApiController {

    private final AuthService authService;
    private final TokenUtil tokenUtil;


    @PostMapping("/api/v1/register")
    @Operation(summary = "회원 등록", description = "회원을 등록합니다.")
    public ResponseEntity<String> register(@RequestBody MemberJoinRequestDto request, HttpServletResponse response) throws JsonProcessingException {
        authService.register(request);
        return ResponseEntity.status(200).body("회원이 등록되었습니다.");
    }

    @PostMapping("/api/v1/login")
    @Operation(summary = "로그인", description = "로그인을 수행합니다.")
    public ResponseEntity<UserModelDto> login(
            @RequestBody MemberLoginDto request, HttpServletResponse response
    ) throws JsonProcessingException {
        UserModelDto dto = authService.login(request);
        String refreshToken = authService.createRefreshToken(request.getUserId());
        authService.setRefreshTokenToCookie(refreshToken, response);
        log.info("refreshToken : " + refreshToken);
        return ResponseEntity.status(200).body(dto);
    }

    @GetMapping("/api/v1/refresh")
    @Operation(summary = "리프레시", description = "토큰을 리프레시합니다.")
    public ResponseEntity<UserModelDto> refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String refreshToken = authService.extractRefreshTokenFromCookie(request);
        String userId = authService.decodeRefreshToken(refreshToken);
        UserModelDto dto = authService.refresh(userId, refreshToken);

        return ResponseEntity.status(200).body(dto);

    }


    @GetMapping("/api/auth/v1/me")
    @Operation(summary = "회원 정보 가져오기", description = "프로필의 회원 정보를 가져옵니다.")
    public ResponseEntity<UserModelDto> getMemberProfile(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = tokenUtil.extractAccessToken(request);
        UserModelDto dto = authService.getMemberProfile(accessToken);
        return ResponseEntity.status(200).body(dto);
    }

    @PostMapping("/api/v1/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 수행합니다.")
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
