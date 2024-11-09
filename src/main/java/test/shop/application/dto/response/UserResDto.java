package test.shop.application.dto.response;

import lombok.Getter;
import test.shop.domain.model.member.Member;

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
