package test.shop.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.shop.domain.model.member.Member;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModelDto {

    private Long id;
    private String name;
    private String role;
    private String password;
    private String userImg;
    private String accessToken;

    public UserModelDto toUserModelDto(Member member, String accessToken) {
        this.id = member.getId();
        this.name = member.getUsername();
        this.role = member.getMemberType().name();
        this.password = member.getPassword();
        this.userImg = member.getUserImg();
        this.accessToken = accessToken;
        return this;
    }
}
