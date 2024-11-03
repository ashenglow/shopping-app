package test.shop.application.dto.request;

import lombok.Getter;
import lombok.Setter;
import test.shop.domain.value.Address;

@Getter
@Setter
public class ProfileDto {
    private Long id;
    private String username;
    private String password;
    private Address address;

}
