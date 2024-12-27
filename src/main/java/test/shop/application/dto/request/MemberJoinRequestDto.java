package test.shop.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.domain.model.member.MemberType;
import test.shop.domain.value.Address;

@Data
@Builder
@AllArgsConstructor
public class MemberJoinRequestDto {
    private Long id;
    private String userId; //for login
    private String email; // required for both regular and Oauth2
    private String nickname; //display name
    private String password;
    private MemberType memberType;
    private Address address;
    private String userImg;
    private String provider;
    private String providerId;

}
