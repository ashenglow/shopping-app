package test.shop.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.shop.domain.model.member.Member;

@Data
@AllArgsConstructor
@Builder
public class UserModelDto {

    private Long id;
    private String userId;
    private String nickname;
    private String email;
    private String role;
    private String password;
    private String userImg;
    private String accessToken;


}
