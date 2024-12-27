package test.shop.infrastructure.oauth2;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import test.shop.infrastructure.oauth2.userinfo.GoogleOAuth2UserInfo;
import test.shop.infrastructure.oauth2.userinfo.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()){
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
//            case "naver":
//                return new NaverOAuth2UserInfo(attributes);
//            case "kakao":
//                return new KakaoOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationException("Sorry, Login with " + registrationId + " is not supported");
        }


    }
}
