package test.shop.web.auth.dto;

import lombok.Getter;
import test.shop.domain.Member;

@Getter
public class UserResDto {

    private final Long memberId;
    private final String username;

    public UserResDto(Long memberId, String username) {
        this.memberId = memberId;
        this.username = username;
    }

    public static UserResDto of(Member member) {
        return new UserResDto(
                member.getId(),
                member.getUsername()
        );
    }
}
