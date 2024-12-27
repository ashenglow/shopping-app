//package test.shop.infrastructure.oauth2.userinfo;
//
//import java.util.Map;
//
//public class NaverOAuth2UserInfo extends OAuth2UserInfo {
//    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
//        super(attributes);
//    }
//    @Override
//    @SuppressWarnings("unchecked")
//    public String getId() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//        return (String) response.get("id");
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public String getName() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//        return (String) response.get("name");
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public String getEmail() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//        return (String) response.get("email");
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public String getImageUrl() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//        return (String) response.get("profile_image");
//    }
//
//    @Override
//    public String getProvider() {
//        return "naver";
//    }
//}
