package test.shop.application.dto.request;

import lombok.*;
import test.shop.domain.value.Address;

@Data
@Builder
@AllArgsConstructor
public class ProfileDto {
    private Long id;
    private String userId;
    private String nickname;
    private String email;
    private String password;
    private Address address;
    private String userImg;

}
