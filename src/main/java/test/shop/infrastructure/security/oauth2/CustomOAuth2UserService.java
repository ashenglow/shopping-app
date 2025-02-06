package test.shop.infrastructure.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.model.member.Member;
import test.shop.domain.model.member.MemberType;
import test.shop.domain.repository.MemberRepository;
import test.shop.domain.value.Address;
import test.shop.infrastructure.security.oauth2.error.OAuth2ErrorType;
import test.shop.infrastructure.security.oauth2.exception.OAuth2AuthenticationProcessingException;
import test.shop.infrastructure.security.oauth2.userinfo.OAuth2UserInfo;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        }catch (DataIntegrityViolationException e){
            if(e.getMessage().contains("Duplicate entry")) {
                throw new OAuth2AuthenticationProcessingException(
                        OAuth2ErrorType.EMAIL_ALREADY_EXISTS,
                        e.getMessage());

            }
            throw new OAuth2AuthenticationProcessingException(
                    OAuth2ErrorType.REGISTRATION_FAILED,
                    e.getMessage());

        }
        catch (Exception e){
            log.error("OAuth2 login error in loading user", e);
            throw new OAuth2AuthenticationProcessingException(
                    OAuth2ErrorType.GENERIC_ERROR,
                    e.getMessage());
        }
    }


    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        if(userInfo.getEmail() == null || userInfo.getEmail().isEmpty()){
            throw new OAuth2AuthenticationProcessingException(
                    OAuth2ErrorType.EMAIL_NOT_FOUND,
                    "Email field is null or empty in OAuth2 response"
            );
        }
        if(userInfo.getId() == null) {
            throw new OAuth2AuthenticationProcessingException(
                    OAuth2ErrorType.MISSING_REQUIRED_INFO,
                    "Provider ID is missing in OAuth2 response");
        }
        Member member = memberRepository.findMemberByProviderAndProviderId(provider, userInfo.getId())
                .map(existingMember -> updateExistingMember(existingMember, userInfo))
                .orElseGet(() -> createNewMember(userInfo, provider));
        return new CustomOAuth2User(oAuth2User, member);

    }

    private Member createNewMember(OAuth2UserInfo userInfo, String provider) {
        String generatedUserId = generateOAuth2UserId(provider, userInfo.getId());

        Member member = Member.builder()
                .userId(generatedUserId)
                .email(userInfo.getEmail())
                .nickname(userInfo.getName())
                .password(passwordEncoder.encode(""))
                .memberType(MemberType.USER)
                .provider(provider)
                .providerId(userInfo.getId())
                .address(new Address("", "", ""))
                .build();

        if(userInfo.getImageUrl() != null) {
            member.addUserImg(userInfo.getImageUrl());
        }
        return memberRepository.save(member);
    }

    private String generateOAuth2UserId(String provider, String providerId) {
        return provider + "_" + providerId;
    }

    private Member updateExistingMember(Member member, OAuth2UserInfo userInfo) {
        if(userInfo.getEmail() != null && !userInfo.getEmail().equals(member.getEmail())) {
            member.updateEmail(userInfo.getEmail());
        }
        if(userInfo.getImageUrl() != null && !userInfo.getImageUrl().equals(member.getUserImg())){
            member.addUserImg(userInfo.getImageUrl());
        }
        return memberRepository.save(member);
    }
}
