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
import test.shop.domain.model.exception.CustomTokenException;
import test.shop.domain.model.member.MemberType;
import test.shop.exception.web.CustomRefreshTokenFailException;
import test.shop.infrastructure.persistence.redis.RedisService;
import test.shop.infrastructure.security.jwt.TokenSubject;
import test.shop.infrastructure.security.jwt.TokenUtil;
import test.shop.application.dto.response.MemberLoginDto;
import test.shop.application.dto.response.UserModelDto;
import test.shop.domain.repository.MemberRepository;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final TokenUtil tokenUtil;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

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
        try {
            String accessToken = tokenUtil.extractAccessToken(request);
            if (accessToken == null) {
                throw new CustomTokenException("Access token not found");
            }
            return tokenUtil.getUserId(accessToken);
        } catch (JsonProcessingException e) {
            log.error("Failed to process token", e);
            throw new CustomTokenException("Invalid token format");
        }

    }

    public String createRefreshToken(String userId) {

        Member member = memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        String uuid = UUID.randomUUID().toString();
        String tokenData = uuid + ":" + userId;
        String refreshToken = Base64.getEncoder().encodeToString(tokenData.getBytes());
        redisService.save(userId, refreshToken, new TokenSubject(member.getId(), userId, member.getMemberType()));
        return refreshToken;
    }

    public String decodeuserIdFromRefreshToken(String refreshToken) {
        try {
            String tokenData = new String(Base64.getDecoder().decode(refreshToken));
            return tokenData.split(":")[1];
        } catch (Exception e) {
            throw new CustomRefreshTokenFailException("Decode userId from refresh token failed: " + e.getMessage());
        }


    }

    public void setRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(604800);
        response.addCookie(cookie);
    }

    public UserModelDto refresh(String userId, String refreshToken){
        try {
            if (refreshToken != null && redisService.existsByRefreshToken(userId, refreshToken)) {
                TokenSubject tokenSubject = redisService.findByRefreshToken(userId, refreshToken);
                String accessToken = tokenUtil.createToken(tokenSubject.getMemberId(), tokenSubject.getUserId(), tokenSubject.getMemberType());
                Member member = memberRepository.findMemberByUserId(userId)
                        .orElseThrow(() -> new CustomRefreshTokenFailException( "Refresh token fail. Member not found" ));
                return member.toUserModelDto(accessToken);


            }
        } catch (Exception e) {
            throw new CustomRefreshTokenFailException("Refresh token not found: " + e.getMessage());
        }
        return null;
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
            if (redisService.existsByRefreshToken(userId, refreshToken)) {
                redisService.delete(userId, refreshToken);
                redisService.setValues(userId, accessToken, "logout", tokenUtil.getAccessTokenExpirationMillis(accessToken), TimeUnit.MILLISECONDS);
                return true;
            }
        } catch (Exception e) {
            throw new CustomTokenException("Refresh token not found: " + e.getMessage());
        }
        return false;
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
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
