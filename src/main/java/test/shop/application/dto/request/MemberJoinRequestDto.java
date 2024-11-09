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
    private String username;
    private String password;
    private MemberType memberType;
    private String userImg;
    private Address address;

}
