package test.shop.web.dto;

import lombok.*;
import test.shop.domain.Member;
import test.shop.domain.MemberType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberAuthDto {

    private Long id;
    private String username;
    private String role;
    private String password;


    public MemberAuthDto MembertoAuthDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.role = member.getMemberType().getRoleName();
        this.password = member.getPassword();
        return this;
    }

}
