package test.shop.infrastructure.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.application.dto.request.MemberJoinRequestDto;
import test.shop.domain.model.member.Member;
import test.shop.common.exception.CustomTokenException;
import test.shop.domain.model.member.MemberType;
import test.shop.common.exception.CustomRefreshTokenFailException;
import test.shop.infrastructure.persistence.redis.RedisService;
import test.shop.infrastructure.security.token.TokenService;
import test.shop.infrastructure.security.token.TokenSubject;
import test.shop.infrastructure.security.token.TokenUtil;
import test.shop.application.dto.response.MemberLoginDto;
import test.shop.application.dto.response.UserModelDto;
import test.shop.domain.repository.MemberRepository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final TokenUtil tokenUtil;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @Transactional(readOnly = false)
    public void register(MemberJoinRequestDto dto) throws JsonProcessingException {
        Member member = buildMemberFromDto(dto);
        memberRepository.save(member);
    }

    @Transactional(readOnly = false)
    public void adminRegister(MemberJoinRequestDto dto) throws JsonProcessingException {
        Member member = buildAdminFromDto(dto);
        memberRepository.save(member);
    }


    private Member buildMemberFromDto(MemberJoinRequestDto dto){
        Member member = Member.builder()
                .userId(dto.getUserId())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .memberType(MemberType.USER)
                .address(dto.getAddress())
                .userImg(dto.getUserImg())
                .provider(dto.getProvider())
                .providerId(dto.getProviderId())
                .build();
        if( dto.getUserImg() == null){
            member.addUserImg("https://i.pravatar.cc/300");
        }
        return member;
    }

    private Member buildAdminFromDto(MemberJoinRequestDto dto){
        Member member = Member.builder()
                .userId(dto.getUserId())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .memberType(MemberType.ADMIN)
                .address(dto.getAddress())
                .userImg(dto.getUserImg())
                .provider(dto.getProvider())
                .providerId(dto.getProviderId())
                .build();

        if( dto.getUserImg() == null){
            member.addUserImg("https://i.pravatar.cc/300");
        }
        return member;
    }

    public UserModelDto login(MemberLoginDto dto) throws JsonProcessingException {
        String userId = dto.getUserId();
        String password = dto.getPassword();

        Member member = memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        if (!member.getPassword().equals(password)) {
            throw new IllegalArgumentException("Password is incorrect");
        }

        String accessToken = tokenUtil.createToken(member.getId(), userId, member.getMemberType());
        return member.toUserModelDto(accessToken);
    }

    public Long getMemberIdFromAccessToken(HttpServletRequest request){

            String accessToken = tokenUtil.extractAccessToken(request);
            if (accessToken == null) {
                throw new CustomTokenException("Access token not found");
            }
            return tokenUtil.getMemberId(accessToken);


    }

    public String getUserIdFromAccessToken(HttpServletRequest request) {

            String accessToken = tokenUtil.extractAccessToken(request);
            if (accessToken == null) {
                throw new CustomTokenException("Access token not found");
            }
            return tokenUtil.getUserId(accessToken);
    }

    public String createRefreshToken(String userId) throws JsonProcessingException {

        Member member = memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        return tokenService.createAndSaveRefreshToken(member);
    }

    public String decodeRefreshToken(String refreshToken) {
       return redisService.decodeUserIdFromRefreshToken(refreshToken);

    }


    public void setRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(604800);
        response.addCookie(cookie);
    }

    public UserModelDto refresh(String userId, String refreshToken){
        try {
            if(refreshToken == null){
                throw new CustomRefreshTokenFailException("Refresh token is missing");
            }
            // redis에서 바로 조회
            TokenSubject tokenSubject = redisService.findByRefreshToken(userId, refreshToken);
            if(tokenSubject == null){
                throw new CustomRefreshTokenFailException("Invalid refresh token");
            }
            // 새 액세스 토큰 생성
            String accessToken = tokenUtil.createToken(
                    tokenSubject.getMemberId(),
                    tokenSubject.getUserId(),
                    tokenSubject.getMemberType());
            // DB에서 사용자 조회
            Member member = memberRepository.findMemberByUserId(userId)
                    .orElseThrow(() -> new CustomRefreshTokenFailException("Member not found"));
            return member.toUserModelDto(accessToken);
        } catch (CustomRefreshTokenFailException e) {
            log.error("[AuthService refresh error]: {}", e.getMessage());
            throw e;
        }catch (Exception e) {
            log.error("[AuthService refresh unexpected error]: {}", e.getMessage());
            throw new CustomRefreshTokenFailException("Unexpected error during refresh: " + e.getMessage());
        }
    }

    public UserModelDto getMemberProfile(String accessToken) throws JsonProcessingException {
        try {
            if (accessToken != null) {

                Long memberId = tokenUtil.getMemberId(accessToken);
                Member member = memberRepository.findMemberById(memberId)
                        .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
                return member.toUserModelDto(accessToken);
            }
        } catch (Exception e) {
            throw new CustomTokenException("Access token not found: " + e.getMessage());
        }
        return null;
    }


    public boolean logout(String accessToken, String refreshToken) {

        try {
            String userId = tokenUtil.getUserId(accessToken);

            // redis에서 바로 조회(한 번만)
            TokenSubject tokenSubject = redisService.findByRefreshToken(userId, refreshToken);
            if(tokenSubject == null){
                throw new CustomTokenException("Invalid refresh token");
            }

            // refresh token 삭제
            redisService.delete(userId, refreshToken);

            // logout 상태 저장(access token 로그아웃 상태로 설정)
            redisService.setValues(
                    userId, accessToken, "logout",
                    tokenUtil.getAccessTokenExpirationMillis(accessToken),TimeUnit.MILLISECONDS
            );

            return true;
        } catch (CustomTokenException e){
            log.error("[AuthService logout error]: {}", e.getMessage());
            throw e;
        }catch (Exception e) {
            log.error("[AuthService logout unexpected error]: {}", e.getMessage());
            throw new CustomTokenException("Unexpected error during logout: " + e.getMessage());
        }

    }

    public void clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    System.out.println("Found refresh token in cookie: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        System.out.println("No refresh token found in cookies");
        return null;
    }
}
