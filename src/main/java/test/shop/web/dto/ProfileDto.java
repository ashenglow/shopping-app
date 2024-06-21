package test.shop.web.dto;

import lombok.Getter;
import lombok.Setter;
import test.shop.domain.Address;

@Getter
@Setter
public class ProfileDto {
    private Long id;
    private String username;
    private String password;
    private Address address;

}
