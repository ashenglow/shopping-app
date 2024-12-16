package test.shop.application.dto.request;

import lombok.Getter;
import lombok.Setter;
import test.shop.domain.value.Address;

@Getter
@Setter
public class ProfileDto {
    private Long id;
    private String userId;
    private String nickname;
    private String email;
    private String password;
    private Address address;

}
